export function setColor(element, color) {
    element.style.color = '#' + color.toString(16).padStart(6, '0');
}