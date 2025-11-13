(function () {
  const ta = document.getElementById("prompt");
  if (!ta) return;

  const autoResize = (el) => {
    el.style.height = "auto";
    el.style.height = el.scrollHeight + "px";
  };

  autoResize(ta);

  ta.addEventListener("input", () => autoResize(ta));
  ta.addEventListener("paste", () =>
    requestAnimationFrame(() => autoResize(ta))
  );
  window.addEventListener("resize", () => autoResize(ta));
})();
