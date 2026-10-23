#!/usr/bin/env python3
"""Helpers for commenting the Java solutions without changing any code.

  python3 tools/codecomments.py check <id> [<id> ...]
      Fails if java/P<id>_*.java differs from the committed version in anything other than
      comments and whitespace (so a comment pass can never change behaviour).
  python3 tools/codecomments.py sync <id> [<id> ...]
      Rewrites every ```java block in p/<id>.md from the current Java file, so the article
      shows the commented code. Blocks are matched by their code (comments ignored), so it can
      be re-run safely after more edits.
  python3 tools/codecomments.py coverage <id> [<id> ...]
      Reports, per article code block, how many "important" lines have a comment on the same
      line or on the line directly above.
"""
import json, os, re, subprocess, sys

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))


def strip_comments(src):
    """Remove // and /* */ comments, keeping string/char/text-block literals and every newline."""
    out, i, n = [], 0, len(src)
    while i < n:
        if src.startswith('"""', i):                       # text block
            j = src.find('"""', i + 3)
            j = n if j < 0 else j + 3
            out.append(src[i:j]); i = j
        elif src[i] in '"\'':                              # string or char literal
            q, j = src[i], i + 1
            while j < n and src[j] != q and src[j] != '\n':
                j += 2 if src[j] == '\\' else 1
            out.append(src[i:j + 1]); i = j + 1
        elif src.startswith('//', i):
            j = src.find('\n', i)
            i = n if j < 0 else j
        elif src.startswith('/*', i):
            j = src.find('*/', i + 2)
            j = n if j < 0 else j + 2
            out.append('\n' * src.count('\n', i, j)); i = j
        else:
            out.append(src[i]); i += 1
    return ''.join(out)


def code_lines(text):
    """[(line_index, normalised code)] for every line that still has code once comments are removed."""
    res = []
    for idx, line in enumerate(strip_comments(text).split('\n')):
        norm = ' '.join(line.split())
        if norm:
            res.append((idx, norm))
    return res


def java_path(pid):
    art = open(os.path.join(ROOT, 'p', f'{pid}.md'), encoding='utf-8').read()
    m = re.search(r'^java:\s*(\S+)', art, re.M)
    return m.group(1) if m else None


def committed(rel):
    try:
        return subprocess.run(['git', 'show', f'HEAD:{rel}'], cwd=ROOT, capture_output=True, text=True, check=True).stdout
    except subprocess.CalledProcessError:
        return None


def cmd_check(ids):
    bad = 0
    for pid in ids:
        rel = java_path(pid)
        old, new = committed(rel), open(os.path.join(ROOT, rel), encoding='utf-8').read()
        if old is None:
            print(f'[{pid}] not committed yet, skipped'); continue
        a, b = [c for _, c in code_lines(old)], [c for _, c in code_lines(new)]
        if a != b:
            bad += 1
            for k, (x, y) in enumerate(zip(a, b)):
                if x != y:
                    print(f'[{pid}] CODE CHANGED near code line {k + 1}:\n   was: {x}\n   now: {y}'); break
            else:
                print(f'[{pid}] CODE CHANGED: {len(a)} code lines before, {len(b)} after')
    print(f'check: {len(ids)} file(s), {bad} with code changes')
    return bad == 0


def find_block(block_lines, new_lines, new_code):
    """Locate an article block inside the Java file by its code lines. Returns (start, end, dedent)."""
    blk_code = code_lines('\n'.join(block_lines))
    if not blk_code:
        return None
    seq = [c for _, c in blk_code]
    codes = [c for _, c in new_code]
    for k in range(len(codes) - len(seq) + 1):
        if codes[k:k + len(seq)] == seq:
            start, end = new_code[k][0], new_code[k + len(seq) - 1][0]
            start -= blk_code[0][0]                                   # leading non-code lines of the block
            end += (len(block_lines) - 1) - blk_code[-1][0]           # trailing non-code lines
            first_blk = block_lines[blk_code[0][0]]
            first_java = new_lines[new_code[k][0]]
            dedent = (len(first_java) - len(first_java.lstrip(' '))) - (len(first_blk) - len(first_blk.lstrip(' ')))
            return max(start, 0), end, max(dedent, 0)
    return None


def cmd_sync(ids):
    ok = True
    for pid in ids:
        apath = os.path.join(ROOT, 'p', f'{pid}.md')
        art = open(apath, encoding='utf-8').read()
        rel = java_path(pid)
        new = open(os.path.join(ROOT, rel), encoding='utf-8').read()
        new_lines, new_code = new.split('\n'), code_lines(new)
        changed = 0

        def repl(m):
            nonlocal changed, ok
            block = m.group(1).rstrip('\n')
            loc = find_block(block.split('\n'), new_lines, new_code)
            if not loc:
                print(f'[{pid}] could not locate a java block starting "{block.splitlines()[0][:50]}"'); ok = False
                return m.group(0)
            s, e, d = loc
            pad = ' ' * d
            fresh = '\n'.join(l[d:] if l.startswith(pad) else l.lstrip(' ') for l in new_lines[s:e + 1])
            if fresh != block:
                changed += 1
            return '```java\n' + fresh + '\n```'

        art2 = re.sub(r'```java\n(.*?)```', repl, art, flags=re.S)
        if art2 != art:
            open(apath, 'w', encoding='utf-8').write(art2)
        print(f'[{pid}] {changed} block(s) updated')
    return ok


IMPORTANT_SKIP = re.compile(r'^[{}()\s;]*$|^(else|try|finally)\b[\s{]*$|^return\s+\w+\s*;$|^break;$|^continue;$')


def cmd_coverage(ids):
    for pid in ids:
        art = open(os.path.join(ROOT, 'p', f'{pid}.md'), encoding='utf-8').read()
        for blk in re.findall(r'```java\n(.*?)```', art, re.S):
            lines = blk.rstrip('\n').split('\n')
            stripped = strip_comments('\n'.join(lines)).split('\n')
            imp = com = 0
            for i, line in enumerate(lines):
                code = stripped[i].strip()
                if not code or IMPORTANT_SKIP.match(code) or i == 0:
                    continue
                imp += 1
                above = lines[i - 1].strip() if i else ''
                if '//' in line[len(stripped[i].rstrip()):] or line.rstrip() != stripped[i].rstrip() or above.startswith('//'):
                    com += 1
            name = lines[0].strip()[:60]
            print(f'[{pid}] {com:3d}/{imp:<3d} commented  {name}')


if __name__ == '__main__':
    if len(sys.argv) < 3 or sys.argv[1] not in ('check', 'sync', 'coverage'):
        print(__doc__); sys.exit(2)
    ids = [int(x) for x in sys.argv[2:]]
    fn = {'check': cmd_check, 'sync': cmd_sync, 'coverage': cmd_coverage}[sys.argv[1]]
    sys.exit(0 if fn(ids) is not False else 1)
