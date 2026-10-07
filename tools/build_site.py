#!/usr/bin/env python3
"""Generate index.md, all.md and topics/*.md from _data/problems.json and the articles present in p/.
Also stamps prev/next navigation into each article's front matter. Run before every commit."""
import json, os, re, collections
ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
problems = json.load(open(os.path.join(ROOT, '_data', 'problems.json'), encoding='utf-8'))

def slugify(s):
    s = re.sub(r"['’]", '', s.lower())
    return re.sub(r'[^a-z0-9]+', '-', s).strip('-')

def written(p):
    return os.path.exists(os.path.join(ROOT, 'p', f"{p['id']}.md"))

by_topic = collections.OrderedDict()
for p in problems:
    by_topic.setdefault(p['topic'], []).append(p)

done_total = sum(1 for p in problems if written(p))
os.makedirs(os.path.join(ROOT, 'topics'), exist_ok=True)

def row(p):
    cls = '' if written(p) else ' class="todo"'
    title = f'<a href="{{{{ "/p/{p["id"]}.html" | relative_url }}}}">{p["title"]}</a>' if written(p) else f'{p["title"]} <small>(coming soon)</small>'
    sheets = ' '.join({'a2z':'A2Z','sde':'SDE','blind75':'B75'}[s] for s in p['sheets'])
    return f'<tr{cls}><td>{p["id"]}</td><td>{title}</td><td><span class="badge badge-{p["difficulty"].lower()}">{p["difficulty"]}</span></td><td>{p["section"]}</td><td>{sheets}</td></tr>'

TABLE_HEAD = '<table class="prob-table"><thead><tr><th>ID</th><th>Problem</th><th>Difficulty</th><th>Section</th><th>Sheets</th></tr></thead><tbody>'

# index.md
pct = round(100 * done_total / len(problems))
lines = ['---', 'title: Topics', 'permalink: /', '---',
         f'<p>{done_total} of {len(problems)} problems written ({pct}%). Every article has a brute-force to optimal walkthrough with Java code that compiles and passes its own tests.</p>',
         f'<div class="progress"><span style="width:{pct}%"></span></div>',
         '<ul class="topic-list">']
for topic, ps in by_topic.items():
    d = sum(1 for p in ps if written(p))
    lines.append(f'<li><a href="{{{{ "/topics/{slugify(topic)}.html" | relative_url }}}}">{topic}</a><span class="count">{d} / {len(ps)} written</span></li>')
lines.append('</ul>')
open(os.path.join(ROOT, 'index.md'), 'w', encoding='utf-8').write('\n'.join(lines) + '\n')

# all.md
lines = ['---', 'title: All problems', '---', f'<p>All {len(problems)} problems in sheet order, keyed by TUF ID.</p>', TABLE_HEAD]
lines += [row(p) for p in problems]
lines.append('</tbody></table>')
open(os.path.join(ROOT, 'all.md'), 'w', encoding='utf-8').write('\n'.join(lines) + '\n')

# topics/*.md
for topic, ps in by_topic.items():
    lines = ['---', f'title: "{topic}"', '---', f'<p><a href="{{{{ "/" | relative_url }}}}">← All topics</a></p>']
    sections = collections.OrderedDict()
    for p in ps:
        sections.setdefault(p['section'], []).append(p)
    for sec, sp in sections.items():
        if sec:
            lines.append(f'<h2>{sec}</h2>')
        lines.append(TABLE_HEAD)
        lines += [row(p) for p in sp]
        lines.append('</tbody></table>')
    open(os.path.join(ROOT, 'topics', f'{slugify(topic)}.md'), 'w', encoding='utf-8').write('\n'.join(lines) + '\n')

# prev/next stamping for written articles (in global sheet order)
written_list = [p for p in problems if written(p)]
for i, p in enumerate(written_list):
    path = os.path.join(ROOT, 'p', f"{p['id']}.md")
    txt = open(path, encoding='utf-8').read()
    m = re.match(r'^---\n(.*?)\n---\n', txt, re.S)
    if not m:
        print('WARN no front matter:', path); continue
    fm = re.sub(r'^(prev_id|prev_title|next_id|next_title):.*\n?', '', m.group(1) + '\n', flags=re.M).rstrip('\n')
    extra = []
    if i > 0:
        q = written_list[i-1]; extra += [f'prev_id: {q["id"]}', f'prev_title: "{q["title"].replace(chr(34), "")}"']
    if i < len(written_list) - 1:
        q = written_list[i+1]; extra += [f'next_id: {q["id"]}', f'next_title: "{q["title"].replace(chr(34), "")}"']
    new = '---\n' + fm + ('\n' + '\n'.join(extra) if extra else '') + '\n---\n' + txt[m.end():]
    if new != txt:
        open(path, 'w', encoding='utf-8').write(new)

print(f'index: {len(by_topic)} topics, {done_total}/{len(problems)} articles written')
