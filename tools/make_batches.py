#!/usr/bin/env python3
"""Split _data/problems.json into writing batches (tools/batches/NN.json), keeping topics contiguous."""
import json, os, collections
ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
problems = json.load(open(os.path.join(ROOT, '_data', 'problems.json'), encoding='utf-8'))
MAX = 12
out_dir = os.path.join(ROOT, 'tools', 'batches'); os.makedirs(out_dir, exist_ok=True)
by_topic = collections.OrderedDict()
for p in problems: by_topic.setdefault(p['topic'], []).append(p)
batches = []
for topic, ps in by_topic.items():
    # chunk each topic into near-equal pieces of <= MAX
    n = (len(ps) + MAX - 1) // MAX
    size = (len(ps) + n - 1) // n
    for i in range(0, len(ps), size):
        batches.append(ps[i:i+size])
# merge tiny trailing batches with a neighbour when total stays <= MAX
merged = []
for b in batches:
    if merged and len(merged[-1]) + len(b) <= MAX and len(b) <= 4:
        merged[-1] = merged[-1] + b
    else:
        merged.append(b)
for f in os.listdir(out_dir):
    if f.endswith('.json'): os.remove(os.path.join(out_dir, f))
for i, b in enumerate(merged, 1):
    json.dump(b, open(os.path.join(out_dir, f'{i:02d}.json'), 'w', encoding='utf-8'), indent=1, ensure_ascii=False)
print(f'{len(merged)} batches, sizes: ' + ' '.join(str(len(b)) for b in merged))
for i, b in enumerate(merged, 1):
    topics = sorted({p['topic'] for p in b}, key=lambda t: [q['topic'] for q in b].index(t))
    print(f'{i:02d}: {len(b):2d}  {" / ".join(t[:38] for t in topics)}  ids {b[0]["id"]}..{b[-1]["id"]}')
