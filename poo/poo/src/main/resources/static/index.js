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


function closeSideBar(){
  const sidebar = document.getElementById("sidebar");
  const body = document.body;

  sidebar.classList.toggle('side-hide');

  // Ajustar margem do body conforme o estado da sidebar
  if (sidebar.classList.contains('side-hide')) {
    body.style.marginLeft = '4rem';
  } else {
    body.style.marginLeft = '16rem';
  }
}

function toggleAnswer(element) {
  const answerDescription = element.querySelector('.answer-description');

  // Verificar se já está revelado
  const isRevealed = answerDescription.style.display === 'block';

  if (!isRevealed) {
    // Revelar TODAS as respostas
    const allItems = document.querySelectorAll('.prompt-context li');

    allItems.forEach(item => {
      const itemDescription = item.querySelector('.answer-description');
      const isCorrect = item.getAttribute('data-correct') === 'true';

      if (itemDescription) {
        itemDescription.style.display = 'block';

        if (isCorrect) {
          item.classList.add('right-answer');
        } else {
          item.classList.add('wrong-answer');
        }
      }
    });
  } else {
    // Ocultar TODAS as respostas
    const allItems = document.querySelectorAll('.prompt-context li');

    allItems.forEach(item => {
      const itemDescription = item.querySelector('.answer-description');

      if (itemDescription) {
        itemDescription.style.display = 'none';
        item.classList.remove('right-answer', 'wrong-answer');
      }
    });
  }
}
