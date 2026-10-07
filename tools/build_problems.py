#!/usr/bin/env python3
"""Merge the raw sheet extracts into data/problems.json (one record per TUF ID)."""
import json, re, os, collections
RAW = os.path.join(os.path.dirname(__file__), '..', 'data', 'raw')
OUT = os.path.join(os.path.dirname(__file__), '..', '_data', 'problems.json')

def slugify(s):
    s = s.lower()
    s = re.sub(r"['’]", '', s)
    s = re.sub(r'[^a-z0-9]+', '-', s).strip('-')
    return s[:60].rstrip('-')

problems = collections.OrderedDict()
def add(pid, title, diff, sheet, info):
    pid = int(pid)
    p = problems.setdefault(pid, {'id': pid, 'title': title, 'difficulty': diff, 'aliases': [], 'sheets': {}})
    if sheet in p['sheets']:
        raise SystemExit(f'duplicate {pid} in {sheet}')
    p['sheets'][sheet] = info
    if title != p['title'] and title not in p['aliases']:
        p['aliases'].append(title)

# A2Z rows 1..360 from the TSV (header is line 5), rows 361..474 from the ;; file
with open(os.path.join(RAW, 'a2z_rows_1_370.tsv'), encoding='utf-8') as f:
    lines = f.read().split('\n')[5:]
rows = [l.split('\t') for l in lines if re.match(r'^\d+\t', l)]
rows = rows[:360]
order = 0
for r in rows:
    order += 1
    add(r[12], r[6].strip(), r[3].strip(), 'a2z',
        {'order': order, 'step': int(r[0]), 'step_name': r[1].strip(), 'section': r[2].strip()})
with open(os.path.join(RAW, 'a2z_rows_361_474.txt'), encoding='utf-8') as f:
    for l in f:
        l = l.rstrip('\n')
        if not l: continue
        step, sname, sec, diff, title, pid = l.split(';;')
        order += 1
        add(pid, title.strip(), diff.strip(), 'a2z',
            {'order': order, 'step': int(step), 'step_name': sname.strip(), 'section': sec.strip()})
a2z_count = sum(1 for p in problems.values() if 'a2z' in p['sheets'])

for fname, sheet, key in (('sde_rows.txt', 'sde', 'day'), ('blind_rows.txt', 'blind75', 'no')):
    with open(os.path.join(RAW, fname), encoding='utf-8') as f:
        for i, l in enumerate(f, 1):
            l = l.rstrip('\n')
            if not l: continue
            num, topic, title, diff, pid = l.split(';;')
            add(pid, title.strip(), diff.strip(), sheet, {'order': i, key: int(num), 'topic': topic.strip()})

for p in problems.values():
    p['slug'] = f"{p['id']}-{slugify(p['title'])}"
    # primary topic for grouping: A2Z step if present, else SDE/Blind topic
    s = p['sheets']
    if 'a2z' in s:
        p['topic'] = s['a2z']['step_name']; p['section'] = s['a2z']['section']
    elif 'sde' in s:
        p['topic'] = s['sde']['topic']; p['section'] = ''
    else:
        p['topic'] = s['blind75']['topic']; p['section'] = ''

out = sorted(problems.values(), key=lambda p: (0 if 'a2z' in p['sheets'] else 1 if 'sde' in p['sheets'] else 2,
                                               p['sheets'].get('a2z', p['sheets'].get('sde', p['sheets'].get('blind75')))['order']))
with open(OUT, 'w', encoding='utf-8') as f:
    json.dump(out, f, indent=1, ensure_ascii=False)
c = collections.Counter(p['difficulty'] for p in out)
print(f"total {len(out)} | a2z {a2z_count} | sde-only+blind-only {len(out)-a2z_count} | {dict(c)}")
print('slug collisions:', len(out) - len({p['slug'] for p in out}))
