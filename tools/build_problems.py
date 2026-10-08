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


# ---------------------------------------------------------------- topic merging
# One topic per subject. A2Z step names become short topic names; the SDE sheet and
# Blind 75 problems that are not on A2Z are placed into the matching A2Z topic and section
# by what the problem actually is (their own sheet topics are kept under p['sheets']).
TOPIC_NAMES = {
    'Learn the basics': 'Basics',
    'Learn Important Sorting Techniques': 'Sorting',
    'Solve Problems on Arrays [Easy -> Medium -> Hard]': 'Arrays',
    'Binary Search [1D, 2D Arrays, Search Space]': 'Binary Search',
    'Strings [Basic and Medium]': 'Strings',
    'Strings': 'Strings',
    'Learn LinkedList [Single LL, Double LL, Medium, Hard Problems]': 'Linked List',
    'Recursion [PatternWise]': 'Recursion and Backtracking',
    'Bit Manipulation [Concepts & Problems]': 'Bit Manipulation',
    'Stack and Queues [Learning, Pre-In-Post-fix, Monotonic Stack, Implementation]': 'Stacks and Queues',
    'Sliding Window & Two Pointer Combined Problems': 'Sliding Window and Two Pointers',
    'Heaps [Learning, Medium, Hard Problems]': 'Heaps',
    'Greedy Algorithms [Easy, Medium/Hard]': 'Greedy Algorithms',
    'Binary Trees [Traversals, Medium and Hard Problems]': 'Binary Trees',
    'Binary Search Trees [Concept and Problems]': 'Binary Search Trees',
    'Graphs [Concepts & Problems]': 'Graphs',
    'Dynamic Programming [Patterns and Problems]': 'Dynamic Programming',
    'Tries': 'Tries',
}
SECTION_RENAMES = {('Strings', 'Hard Problems'): 'Hard String Problems'}

AR, SW, BS, ST, LL = 'Arrays', 'Sliding Window and Two Pointers', 'Binary Search', 'Strings', 'Linked List'
RC, BM, SQ, HP, GR = 'Recursion and Backtracking', 'Bit Manipulation', 'Stacks and Queues', 'Heaps', 'Greedy Algorithms'
BT, BST, GP, DP = 'Binary Trees', 'Binary Search Trees', 'Graphs', 'Dynamic Programming'
EXTRA = {
    # Arrays
    36: (AR, 'Medium'), 465: (AR, 'Medium'), 2758: (AR, 'Hard'), 563: (AR, 'Medium'), 2837: (AR, 'Easy'),
    47: (AR, 'Easy'), 2392: (AR, 'Easy'), 2404: (AR, 'Medium'), 2760: (AR, 'Medium'), 2759: (AR, 'Hard'),
    # Sliding window and two pointers
    929: (SW, 'Medium Problems'), 2396: (SW, 'Medium Problems'), 279: (SW, 'Medium Problems'),
    # Binary search
    2771: (BS, 'BS on Answers'), 90: (BS, 'BS on 1D Arrays'), 2769: (BS, 'BS on 1D Arrays'),
    76: (BS, 'BS on Answers'), 2766: (BS, 'BS on Answers'),
    # Strings
    2852: (ST, 'Medium String Problems'), 2861: (ST, 'Medium String Problems'), 312: (ST, 'Hard String Problems'),
    400: (ST, 'Basic and Easy String Problems'), 198: (ST, 'Medium String Problems'), 559: (ST, 'Medium String Problems'),
    2794: (ST, 'Medium String Problems'), 331: (ST, 'Medium String Problems'),
    # Linked list
    448: (LL, 'Medium Problems of LL'), 613: (LL, 'Medium Problems of LL'), 626: (LL, 'Medium Problems of LL'),
    2848: (LL, 'Medium Problems of LL'), 258: (LL, 'Medium Problems of LL'), 621: (LL, 'Medium Problems of LL'),
    620: (LL, 'Medium Problems of LL'), 618: (LL, 'Medium Problems of LL'), 622: (LL, 'Medium Problems of LL'),
    2407: (LL, 'Medium Problems of LL'), 614: (LL, 'Hard Problems of LL'), 612: (LL, 'Hard Problems of LL'),
    615: (LL, 'Hard Problems of LL'),
    # Recursion and backtracking
    2805: (RC, 'Subsequences Pattern'), 2856: (RC, 'Subsequences Pattern'), 818: (RC, 'Trying out all Combos / Hard'),
    821: (RC, 'Trying out all Combos / Hard'), 2757: (RC, 'Trying out all Combos / Hard'),
    # Bit manipulation
    2411: (BM, 'Interview Problems'), 2402: (BM, 'Interview Problems'), 2393: (BM, 'Interview Problems'),
    2406: (BM, 'Interview Problems'),
    # Stacks and queues
    2808: (SQ, 'Learning'), 936: (SQ, 'Learning'), 687: (SQ, 'Monotonic Stack/Queue Problems [VVV. Imp]'),
    # Heaps
    575: (HP, 'Learning'), 713: (HP, 'Medium Problems'), 2839: (HP, 'Medium Problems'),
    446: (HP, 'Hard Problems'), 567: (HP, 'Hard Problems'),
    # Greedy
    319: (GR, 'Easy Problems'), 709: (GR, 'Medium/Hard'), 710: (GR, 'Medium/Hard'),
    # Binary trees
    133: (BT, 'Traversals'), 2790: (BT, 'Traversals'), 139: (BT, 'Hard Problems'), 138: (BT, 'Hard Problems'),
    123: (BT, 'Medium Problems'), 109: (BT, 'Hard Problems'), 128: (BT, 'Medium Problems'),
    823: (BT, 'Medium Problems'), 62: (BT, 'Hard Problems'), 2783: (BT, 'Medium Problems'),
    2408: (BT, 'Medium Problems'),
    # Binary search trees
    108: (BST, 'Concepts'), 2391: (BST, 'Practice Problems'), 103: (BST, 'Practice Problems'),
    2779: (BST, 'Practice Problems'), 2777: (BST, 'Practice Problems'), 2776: (BST, 'Practice Problems'),
    99: (BST, 'Practice Problems'), 96: (BST, 'Practice Problems'), 2773: (BST, 'Practice Problems'),
    # Graphs
    188: (GP, 'Problems on BFS/DFS'), 2818: (GP, 'Problems on BFS/DFS'), 2819: (GP, 'Problems on BFS/DFS'),
    2833: (GP, 'Problems on BFS/DFS'), 499: (GP, 'Problems on BFS/DFS'), 2812: (GP, 'Problems on BFS/DFS'),
    2832: (GP, 'Problems on BFS/DFS'), 2403: (GP, 'Problems on BFS/DFS'), 495: (GP, 'Problems on BFS/DFS'),
    2816: (GP, 'Topo Sort and Problems'), 2815: (GP, 'Topo Sort and Problems'), 2820: (GP, 'Topo Sort and Problems'),
    2821: (GP, 'Topo Sort and Problems'), 520: (GP, 'Shortest Path Algorithms and Problems'),
    518: (GP, 'Shortest Path Algorithms and Problems'), 2829: (GP, 'Shortest Path Algorithms and Problems'),
    2824: (GP, 'MinimumSpanningTree/Disjoint Set and Problems'), 2823: (GP, 'MinimumSpanningTree/Disjoint Set and Problems'),
    2811: (GP, 'Other Algorithms'),
    # Dynamic programming
    297: (DP, '2D/3D DP and DP on Grids'), 2797: (DP, '2D/3D DP and DP on Grids'), 315: (DP, 'DP on Subsequences'),
    316: (DP, 'DP on Subsequences'), 323: (DP, 'DP on Subsequences'), 322: (DP, 'DP on Subsequences'),
    698: (DP, 'DP on LIS'), 1004: (DP, 'MCM DP - Partition DP'), 2854: (DP, 'MCM DP - Partition DP'),
    691: (DP, '1D DP'), 2397: (DP, '1D DP'),
}

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
    s = p['sheets']
    if 'a2z' in s:
        t = TOPIC_NAMES[s['a2z']['step_name']]
        p['topic'] = t
        p['section'] = SECTION_RENAMES.get((t, s['a2z']['section']), s['a2z']['section'])
    else:
        if p['id'] not in EXTRA:
            raise SystemExit(f"problem {p['id']} ({p['title']}) has no topic mapping in EXTRA")
        p['topic'], p['section'] = EXTRA[p['id']]

# every mapped section must be one of that topic's A2Z sections (catches typos)
known = collections.defaultdict(set)
for p in problems.values():
    if 'a2z' in p['sheets']: known[p['topic']].add(p['section'])
for pid, (t, sec) in EXTRA.items():
    if pid not in problems: raise SystemExit(f"EXTRA has unknown id {pid}")
    if sec not in known.get(t, ()): raise SystemExit(f"EXTRA {pid}: section {sec!r} not in topic {t!r}")

out = sorted(problems.values(), key=lambda p: (0 if 'a2z' in p['sheets'] else 1 if 'sde' in p['sheets'] else 2,
                                               p['sheets'].get('a2z', p['sheets'].get('sde', p['sheets'].get('blind75')))['order']))
with open(OUT, 'w', encoding='utf-8') as f:
    json.dump(out, f, indent=1, ensure_ascii=False)
c = collections.Counter(p['difficulty'] for p in out)
print(f"total {len(out)} | a2z {a2z_count} | sde-only+blind-only {len(out)-a2z_count} | {dict(c)}")
print('slug collisions:', len(out) - len({p['slug'] for p in out}))
