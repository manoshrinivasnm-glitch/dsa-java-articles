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

  // ---------- jump to the optimal approach ----------
  // Rule: the first "Approach ..." heading that says "optimal"; otherwise the last approach
  // (articles run from brute force to the best approach).
  var approaches = Array.prototype.filter.call(article.querySelectorAll('h2'), function (h) {
    return /^approach\b/i.test(h.textContent.trim());
  });
  if (!approaches.length) return;
  var heading = approaches.filter(function (h) { return /\boptimal\b/i.test(h.textContent); })[0]
    || approaches[approaches.length - 1];

  // first code block between this heading and the next h2
  var target = null;
  for (var el = heading.nextElementSibling; el && el.tagName !== 'H2'; el = el.nextElementSibling) {
    if (el.matches('div.highlighter-rouge')) { target = el; break; }
    var inner = el.querySelector && el.querySelector('div.highlighter-rouge');
    if (inner) { target = inner; break; }
  }
  target = target || heading;
  target.id = target.id || 'optimal-code';

  var jump = document.querySelector('.jump-optimal');
  if (!jump) return;
  jump.hidden = false;
  jump.title = heading.textContent.trim();
  function go(smooth) {
    target.scrollIntoView({ behavior: smooth ? 'smooth' : 'auto', block: 'start' });
    target.classList.remove('flash');
    void target.offsetWidth; // restart the animation
    target.classList.add('flash');
  }
  jump.addEventListener('click', function () {
    go(true);
    if (history.replaceState) history.replaceState(null, '', '#optimal');
  });
  if (location.hash === '#optimal') setTimeout(function () { go(false); }, 0);
})();
