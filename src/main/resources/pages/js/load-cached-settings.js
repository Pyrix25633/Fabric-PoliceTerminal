const compactModeCssLink = document.getElementById('compact-mode-css');
const sharpModeCssLink = document.getElementById('sharp-mode-css');
const fontCssLink = document.getElementById('font-css');

let cachedSettings = JSON.parse(localStorage.getItem('cachedSettings'));

if(cachedSettings == null) {
    cachedSettings = {
        compactMode: false,
        condensedFont: false,
        sharpMode: false
    }
}

compactModeCssLink.href = './css/compact-mode-' + (cachedSettings.compactMode ? 'on': 'off') + '.css';
fontCssLink.href = './css/roboto-condensed-' + (cachedSettings.condensedFont ? 'on': 'off') + '.css';
sharpModeCssLink.href = './css/sharp-mode-' + (cachedSettings.sharpMode ? 'on': 'off') + '.css';