function toggleElement(elementId, displayStyle) {
    let element = document.getElementById(elementId);
    let current = element.currentStyle
        ? element.currentStyle['display']
        : document.defaultView.getComputedStyle(element, null).getPropertyValue('display');
    element.style.display = (current == 'none' ? displayStyle : 'none');
}

function toggle(toggleId) {
    let toggle = document.getElementById ? document.getElementById(toggleId) : document.all[toggleId];
    toggle.textContent = toggle.innerHTML == '\u25b6' ? '\u25bc' : '\u25b6';
}

function modalClose() {
    let modal = document.getElementById("modal");
    modal.style.display = "none";
}

function modalImage(screenshotId) {
    let screenshot = document.getElementById("screenshot-" + screenshotId);
    let modal = document.getElementById("modal");
    let modalImg = document.getElementById("modalImage");

    modalImg.src = screenshot.src;
    modal.style.display = "block";
}