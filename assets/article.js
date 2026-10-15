// Article page helpers: a copy button on every code block, and a jump to the optimal approach's code.
(function () {
  var article = document.querySelector('article.article');
  if (!article) return;

  // ---------- copy buttons ----------
  function copyText(text, done) {
    if (navigator.clipboard && window.isSecureContext) {
      navigator.clipboard.writeText(text).then(done, function () { fallback(text, done); });
    } else {
      fallback(text, done);
    }
  }
  function fallback(text, done) {
    var ta = document.createElement('textarea');
    ta.value = text;
    ta.setAttribute('readonly', '');
    ta.style.position = 'fixed';
    ta.style.top = '-1000px';
    document.body.appendChild(ta);
    ta.select();
    try { document.execCommand('copy'); done(); } catch (e) { /* nothing else to try */ }
    document.body.removeChild(ta);
  }
  function sectionOf(el) {
    // text of the nearest h2 above this element
    var all = article.querySelectorAll('h2');
    var name = '';
    for (var i = 0; i < all.length; i++) {
      if (all[i].compareDocumentPosition(el) & Node.DOCUMENT_POSITION_FOLLOWING) name = all[i].textContent.trim();
    }
    return name;
  }

  var blocks = article.querySelectorAll('div.highlighter-rouge');
  blocks.forEach(function (wrap) {
    var code = wrap.querySelector('pre code') || wrap.querySelector('pre');
    if (!code) return;
    var isJava = /\blanguage-java\b/.test(wrap.className);
    var isTest = !isJava && /^problem$/i.test(sectionOf(wrap));
    var label = isJava ? 'Java' : (isTest ? 'Test case' : 'Text');

    var bar = document.createElement('div');
    bar.className = 'code-bar';
    var span = document.createElement('span');
    span.textContent = label;
    var btn = document.createElement('button');
    btn.type = 'button';
    btn.className = 'copy-btn';
    btn.textContent = 'Copy';
    btn.setAttribute('aria-label', isJava ? 'Copy code' : (isTest ? 'Copy test case' : 'Copy text'));
    btn.addEventListener('click', function () {
      var text = code.innerText.replace(/\n+$/, '');
      copyText(text, function () {
        btn.textContent = 'Copied';
        btn.classList.add('copied');
        btn.dataset.copiedLength = String(text.length);
        clearTimeout(btn._t);
        btn._t = setTimeout(function () { btn.textContent = 'Copy'; btn.classList.remove('copied'); }, 1500);
      });
    });
    bar.appendChild(span);
    bar.appendChild(btn);
    wrap.insertBefore(bar, wrap.firstChild);
    wrap.classList.add('has-bar');
  });

  // ---------- previous / next problem, repeated where readers stop ----------
  // The layout renders one pager at the bottom; copies go under "Jump to code" and after
  // the Brute, Better and Optimal solutions.
  var pager = article.querySelector('nav.pager');
  var placed = [];
  function addPager(after) {
    if (!pager || !after || placed.indexOf(after) !== -1) return;
    placed.push(after);
    var copy = pager.cloneNode(true);
    copy.classList.add('pager-inline');
    after.insertAdjacentElement('afterend', copy);
  }
  addPager(document.querySelector('.actions') || document.querySelector('.links'));

  // ---------- jump to brute / better / optimal ----------
  // Optimal: the first "Approach ..." heading that says "optimal", else the last approach.
  // Brute:   the first heading that says "brute", else the first approach.
  // Better:  a heading that says "better" between those two, else the last approach between them;
  //          none when the article goes straight from brute force to optimal.
  var approaches = Array.prototype.filter.call(article.querySelectorAll('h2'), function (h) {
    return /^approach\b/i.test(h.textContent.trim());
  });
  var row = document.querySelector('.actions');
  if (!approaches.length || !row) return;

  function idx(re, from, to) {
    for (var i = from; i < to; i++) if (re.test(approaches[i].textContent)) return i;
    return -1;
  }
  var n = approaches.length;
  var optimal = idx(/\boptimal\b/i, 0, n);
  if (optimal < 0) optimal = n - 1;
  var brute = idx(/\bbrute\b/i, 0, n);
  if (brute < 0 || brute >= optimal) brute = 0;
  var better = idx(/\bbetter\b/i, brute + 1, optimal);
  if (better < 0) better = optimal - 1 > brute ? optimal - 1 : -1;

  // the first Java block in a section (else the first code block of any kind, else the heading)
  function codeOf(heading) {
    var anyBlock = null;
    for (var el = heading.nextElementSibling; el && el.tagName !== 'H2'; el = el.nextElementSibling) {
      var found = el.matches('div.highlighter-rouge') ? [el] : Array.prototype.slice.call(el.querySelectorAll ? el.querySelectorAll('div.highlighter-rouge') : []);
      for (var k = 0; k < found.length; k++) {
        anyBlock = anyBlock || found[k];
        if (/\blanguage-java\b/.test(found[k].className)) return found[k];
      }
    }
    return anyBlock || heading;
  }

  // the last Java block in a section: the end of that solution's code
  function endOfCode(heading) {
    var last = null;
    for (var el = heading.nextElementSibling; el && el.tagName !== 'H2'; el = el.nextElementSibling) {
      var found = el.matches('div.highlighter-rouge') ? [el] : Array.prototype.slice.call(el.querySelectorAll ? el.querySelectorAll('div.highlighter-rouge') : []);
      found.forEach(function (b) { if (/\blanguage-java\b/.test(b.className)) last = b; });
    }
    return last;
  }

  var reduceMotion = window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches;
  function go(target, smooth) {
    target.scrollIntoView({ behavior: smooth && !reduceMotion ? 'smooth' : 'auto', block: 'start' });
    target.classList.remove('flash');
    void target.offsetWidth; // restart the animation
    target.classList.add('flash');
  }

  var plan = { brute: brute, better: better, optimal: optimal };
  var NAMES = { brute: 'brute force', better: 'better', optimal: 'optimal' };
  var targets = {};
  Object.keys(plan).forEach(function (key) {
    var btn = row.querySelector('[data-jump="' + key + '"]');
    if (!btn) return;
    var i = plan[key];
    // a single-approach article: that approach is the optimal one only
    if (i < 0 || (key !== 'optimal' && i === optimal)) {
      btn.disabled = true;
      btn.title = n === 1 ? 'This problem has only one approach'
        : key === 'better' ? 'This problem goes straight from brute force to the optimal approach'
        : 'The first approach here is already the optimal one';
      btn.setAttribute('aria-label', 'Jump to ' + NAMES[key] + ' code (not available)');
      return;
    }
    var heading = approaches[i], target = codeOf(heading), label = heading.textContent.trim();
    targets[key] = target;
    if (!target.id) target.id = key + '-code';
    btn.title = label;
    btn.setAttribute('aria-label', 'Jump to ' + NAMES[key] + ' code: ' + label);
    btn.addEventListener('click', function () {
      go(target, true);
      if (history.replaceState) history.replaceState(null, '', '#' + key);
    });
  });
  row.hidden = false;
  ['brute', 'better', 'optimal'].forEach(function (key) {
    if (targets[key]) addPager(endOfCode(approaches[plan[key]]));
  });

  var h = location.hash.replace('#', '');
  if (targets[h]) setTimeout(function () { go(targets[h], false); }, 0);
})();
