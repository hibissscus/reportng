function toggleElement(elementId, displayStyle) {
    let element = document.getElementById(elementId);
    let current = element.currentStyle
        ? element.currentStyle['display']
        : document.defaultView.getComputedStyle(element, null).getPropertyValue('display');
    element.style.display = (current === 'none' ? displayStyle : 'none');
}

function toggleElements(elementClass, displayStyle) {
    let elements = document.getElementsByClassName(elementClass);
    for (let element of elements) {
        let current = element.currentStyle
            ? element.currentStyle['display']
            : document.defaultView.getComputedStyle(element, null).getPropertyValue('display');
        element.style.display = (current === 'none' ? displayStyle : 'none');
    }
}

function toggle(toggleId) {
    let toggle = document.getElementById ? document.getElementById(toggleId) : document.all[toggleId];
    toggle.textContent = toggle.innerHTML === '\u25b6' ? '\u25bc' : '\u25b6';
}

function modalClose(modalId) {
    let modal = document.getElementById("modal-" + modalId);
    modal.style.display = "none";
    let deck = new Reveal(document.querySelector('.deck' + modalId));
    deck.destroy();
}

function modalImage(modalId, screenshotId) {
    let modal = document.getElementById("modal-" + modalId,);
    let deck = new Reveal(document.querySelector('.deck' + modalId,), {
        embedded: true,
        controls: false,
        transition: 'none',
        slideNumber: "c/t",
    });
    deck.addEventListener('ready', function (event) {
        let indices = deck.getIndices(document.getElementById(screenshotId));
        deck.slide(indices.h, indices.v);
    });
    deck.initialize();
    modal.style.display = "block";
}