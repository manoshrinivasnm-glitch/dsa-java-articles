#!/usr/bin/env python3
"""Generate index.md, all.md and topics/*.md from _data/problems.json and the articles present in p/.
Also stamps prev/next navigation into each article's front matter. Run before every commit."""
import json, os, re, collections
ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
problems = json.load(open(os.path.join(ROOT, '_data', 'problems.json'), encoding='utf-8'))

def slugify(s):
    s = re.sub(r"['’]", '', s.lower())
    return re.sub(r'[^a-z0-9]+', '-', s).strip('-')

import subprocess
def _published_ids():
    """Articles count as written only once they are tracked or staged in git, so pages for
    batches still being drafted never appear in the index."""
    try:
        out = subprocess.run(['git', 'ls-files', '--cached', 'p/'], cwd=ROOT, capture_output=True, text=True, check=True).stdout
    except Exception:
        return None
    return {int(os.path.basename(f)[:-3]) for f in out.split() if os.path.basename(f)[:-3].isdigit()}
_PUB = _published_ids()
def written(p):
    exists = os.path.exists(os.path.join(ROOT, 'p', f"{p['id']}.md"))
    return exists and (_PUB is None or p['id'] in _PUB)

by_topic = collections.OrderedDict()
for p in problems:
    by_topic.setdefault(p['topic'], []).append(p)

done_total = sum(1 for p in problems if written(p))
os.makedirs(os.path.join(ROOT, 'topics'), exist_ok=True)
# drop topic pages for topics that no longer exist (e.g. after merging)
for f in os.listdir(os.path.join(ROOT, 'topics')):
    if f.endswith('.md'): os.remove(os.path.join(ROOT, 'topics', f))

import html
SHEET_LABEL = {'a2z': 'A2Z', 'sde': 'SDE', 'blind75': 'B75'}
SHEET_WORDS = {'a2z': 'a2z', 'sde': 'sde', 'blind75': 'b75 blind 75 blind75'}

def norm(*parts):
    """Lower-case, drop apostrophes, everything else non-alphanumeric becomes a space (mirrors search.js)."""
    t = ' '.join(str(x) for x in parts if x).lower()
    t = re.sub(r"['\u2019]", '', t)
    return re.sub(r'[^a-z0-9]+', ' ', t).strip()

# the sheets misspell a few names; index the usual spelling too so either one finds the problem
SPELLINGS = {'djisktra': 'dijkstra', 'paranthesis': 'parenthesis parentheses', 'parentheses': 'parenthesis', 'parenthesis': 'parentheses'}

def name_text(p):
    n = norm(p['title'], ' '.join(p['aliases']))
    extra = [v for k, v in SPELLINGS.items() if k in n and v not in n]
    squashed = n.replace(' ', '')            # lets "nqueen" or "lrucache" match
    return ' '.join([n] + extra + [squashed])

def search_text(p):
    return norm(p['id'], name_text(p), p['topic'], p['section'], p['difficulty'],
                ' '.join(SHEET_WORDS[s] for s in p['sheets']))

def row(p):
    e = html.escape
    cls = '' if written(p) else ' class="todo"'
    if written(p):
        title = f'<a href="{{{{ "/p/{p["id"]}.html" | relative_url }}}}" target="_blank" rel="noopener">{e(p["title"])}</a>'
    else:
        title = f'{e(p["title"])} <small>(coming soon)</small>'
    sheets = ' '.join(SHEET_LABEL[s] for s in p['sheets'])
    return (f'<tr{cls} data-search="{e(search_text(p))}"><td>{p["id"]}</td><td>{title}</td>'
            f'<td><span class="badge badge-{p["difficulty"].lower()}">{p["difficulty"]}</span></td>'
            f'<td>{e(p["section"])}</td><td>{sheets}</td></tr>')

def search_box(mode, placeholder, extra=''):
    return (f'<div class="search" data-mode="{mode}"{extra}>'
            f'<input id="problem-search" type="search" placeholder="{placeholder}" aria-label="Search problems" autocomplete="off" spellcheck="false">'
            f'<p class="search-status" aria-live="polite"></p></div>')

SEARCH_SCRIPT = '<script src="{{ \'/assets/search.js\' | relative_url }}?v={{ site.time | date: \'%s\' }}" defer></script>'

TABLE_HEAD = '<table class="prob-table"><thead><tr><th>ID</th><th>Problem</th><th>Difficulty</th><th>Section</th><th>Sheets</th></tr></thead><tbody>'

# index.md
pct = round(100 * done_total / len(problems))
lines = ['---', 'title: Topics', 'permalink: /', '---',
         f'<p>{done_total} of {len(problems)} problems written ({pct}%). Every article has a brute-force to optimal walkthrough with Java code that compiles and passes its own tests.</p>',
         f'<div class="progress"><span style="width:{pct}%"></span></div>',
         search_box('index', f'Search {len(problems)} problems by name, topic or TUF ID',
                    ' data-index="{{ \'/assets/search-index.json\' | relative_url }}" data-all="{{ \'/all.html\' | relative_url }}"'),
         '<ul class="search-results" hidden></ul>',
         '<ul class="topic-list">']
for topic, ps in by_topic.items():
    d = sum(1 for p in ps if written(p))
    lines.append(f'<li><a href="{{{{ "/topics/{slugify(topic)}.html" | relative_url }}}}">{topic}</a><span class="count">{d} / {len(ps)} written</span></li>')
lines.append('</ul>')
lines.append(SEARCH_SCRIPT)
open(os.path.join(ROOT, 'index.md'), 'w', encoding='utf-8').write('\n'.join(lines) + '\n')

# all.md
lines = ['---', 'title: All problems', '---', f'<p>All {len(problems)} problems in sheet order, keyed by TUF ID.</p>',
         search_box('table', 'Filter by name, topic, section, difficulty or TUF ID'), TABLE_HEAD]
lines += [row(p) for p in problems]
lines.append('</tbody></table>')
lines.append(SEARCH_SCRIPT)
open(os.path.join(ROOT, 'all.md'), 'w', encoding='utf-8').write('\n'.join(lines) + '\n')

# topics/*.md
for topic, ps in by_topic.items():
    lines = ['---', f'title: "{topic}"', '---', f'<p><a href="{{{{ "/" | relative_url }}}}">← All topics</a></p>',
             search_box('table', f'Filter {len(ps)} {html.escape(topic)} problems')]
    sections = collections.OrderedDict()
    for p in ps:
        sections.setdefault(p['section'], []).append(p)
    for sec, sp in sections.items():
        if sec:
            lines.append(f'<h2>{sec}</h2>')
        lines.append(TABLE_HEAD)
        lines += [row(p) for p in sp]
        lines.append('</tbody></table>')
    lines.append(SEARCH_SCRIPT)
    open(os.path.join(ROOT, 'topics', f'{slugify(topic)}.md'), 'w', encoding='utf-8').write('\n'.join(lines) + '\n')

# assets/search-index.json: what the Topics page searches
index = [{'id': p['id'], 't': p['title'], 'd': p['difficulty'], 'p': p['topic'], 's': p['section'],
          'h': [SHEET_LABEL[x] for x in p['sheets']], 'n': name_text(p), 'q': search_text(p)}
         for p in problems if written(p)]
with open(os.path.join(ROOT, 'assets', 'search-index.json'), 'w', encoding='utf-8') as f:
    json.dump(index, f, ensure_ascii=False, separators=(',', ':'))

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
