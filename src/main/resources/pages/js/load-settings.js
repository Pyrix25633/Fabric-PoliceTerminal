const compactModeCssLink = document.getElementById('compact-mode-css');
const sharpModeCssLink = document.getElementById('sharp-mode-css');
const fontCssLink = document.getElementById('font-css');

export const statusCodeActions = {
    400: () => {
        console.log('Error 400: Bad Request');
    },
    401: () => {
        window.location.href = '/login';
    },
    403: () => {
        window.location.href = '/403.html';
    },
    404: () => {
        window.location.href = '/404.html';
    },
    405: () => {
        window.location.href = '/405.html';
    },
    500: () => {
        window.location.href = '/500.html';
    }
};

export function loadSettings(callback, whenFinished) {
    $.ajax({
        url: '/api/auth/validate-token',
        method: 'GET',
        success: (res) => {
            if(res.valid) {
                if(whenFinished) {
                    getSettings(callback);
                }
                else {
                    getSettings();
                    if(typeof callback == 'function')
                        callback();
                }
            }
            else
                window.location.href = '/login';
        },
        statusCode: statusCodeActions
    });
}

function getSettings(callback) {
    $.ajax({
        url: '/api/auth/settings',
        method: 'GET',
        success: (res) => {
            showSettings(res);
            if(typeof callback == 'function')
                callback(res);
        },
        statusCode: statusCodeActions
    });
}

function showSettings(settings) {
    compactModeCssLink.href = './css/compact-mode-' + (settings.compactMode ? 'on': 'off') + '.css';
    fontCssLink.href = './css/roboto-condensed-' + (settings.condensedFont ? 'on': 'off') + '.css';
    sharpModeCssLink.href = './css/sharp-mode-' + (settings.sharpMode ? 'on': 'off') + '.css';
    localStorage.setItem('cachedSettings', JSON.stringify(settings));
}