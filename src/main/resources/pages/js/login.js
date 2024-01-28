const callsignInput = document.getElementById('callsign');
const callsignFeedbackSpan = document.getElementById('callsign-feedback');
const passwordInput = document.getElementById('password');
const passwordFeedbackSpan = document.getElementById('password-feedback');
const loginButton = document.getElementById('login');

let validCallsign = false;
let validPassword = false;
loginButton.disabled = true;

loginButton.addEventListener('click', async () => {
    if(!(validCallsign && validPassword)) return;
    $.ajax({
        url: '/api/user/login',
        method: 'POST',
        data: JSON.stringify({
            callsign: callsignInput.value,
            password: passwordInput.value
        }),
        contentType: 'application/json',
        success: (res) => {
            localStorage.setItem('cachedLogin', JSON.stringify(res));
            window.location.href = '/';
        },
        statusCode: {
            400: () => {
                console.log('Error 400: Bad Request');
            },
            401: () => {
                passwordFeedbackSpan.innerText = "Wrong Password!";
                passwordFeedbackSpan.classList.replace('success', 'error');
                validPassword = false;
                loginButton.disabled = true;
            },
            404: () => {
                callsignFeedbackSpan.innerText = "Callsign not found!";
                callsignFeedbackSpan.classList.replace('success', 'error');
                validCallsign = false;
                loginButton.disabled = true;
            },
            500: () => {
                console.log('Error 500: Internal Server Error');
            }
        }
    });
});

let automaticInsertionInterval = setInterval(() => {
    if(callsignInput.value != '') callsignTyped();
    if(passwordInput.value != '') passwordTyped();
    clearInterval(automaticInsertionInterval);
}, 250);

let callsignTimer;
callsignInput.addEventListener('keyup', () => {
    clearTimeout(callsignTimer);
    callsignTimer = setTimeout(callsignTyped, 1000);
});
callsignInput.addEventListener('keydown', () => {
    clearTimeout(callsignTimer);
});
callsignInput.addEventListener('focusout', () => {
    clearTimeout(callsignTimer);
    callsignTyped();
});
function callsignTyped() {
    callsignFeedbackSpan.classList.add('error');
    $.ajax({
        url: '/api/user/callsign-login-feedback?callsign=' + callsignInput.value,
        method: 'GET',
        dataType: 'json',
        success: (res) => {
            callsignFeedbackSpan.innerText = res.feedback;
            if(res.feedback.includes('!')) {
                callsignFeedbackSpan.classList.replace('success', 'error');
                validCallsign = false;
                loginButton.disabled = true;
                return;
            }
            callsignFeedbackSpan.classList.replace('error', 'success');
            validCallsign = true;
            loginButton.disabled = !(validCallsign && validPassword);
        },
        error: (req, err) => {
            console.log(err);
            callsignFeedbackSpan.innerText = 'Server unreachable!';
            callsignFeedbackSpan.classList.replace('success', 'error');
        }
    });
}

let passwordTimer;
passwordInput.addEventListener('keyup', () => {
    clearTimeout(passwordTimer);
    passwordTimer = setTimeout(passwordTyped, 1000);
});
passwordInput.addEventListener('keydown', () => {
    clearTimeout(passwordTimer);
});
passwordInput.addEventListener('focusout', () => {
    clearTimeout(passwordTimer);
    passwordTyped();
});
function passwordTyped() {
    passwordFeedbackSpan.classList.add('error');
    if(passwordInput.value.length != 8) {
        passwordFeedbackSpan.innerText = 'Invalid Password!'
        passwordFeedbackSpan.classList.replace('success', 'error');
        validPassword = false;
        loginButton.disabled = true;
        console.log("1");
        return;
    }
    let digits = 0, symbols = 0;
    for(let i = 0; i < passwordInput.value.length; i++) {
        let c = passwordInput.value.codePointAt(i);
        if(c >= 48 && c <= 57) digits++;
        else if((c >= 45 && c <= 47) || c == 35 || c == 64 || c == 42 || c == 95) symbols++;
        else if(!((c >= 97 && c <= 122) || (c >= 65 && c <= 90))) {
            passwordFeedbackSpan.innerText = 'Invalid Password!'
            passwordFeedbackSpan.classList.replace('success', 'error');
            validPassword = false;
            loginButton.disabled = true;
            console.log(c);
            return;
        }
    }
    if(digits < 1 || symbols < 1) {
        passwordFeedbackSpan.innerText = 'Invalid Password!'
        passwordFeedbackSpan.classList.replace('success', 'error');
        validPassword = false;
        loginButton.disabled = true;
        console.log("3");
    }
    else {
        passwordFeedbackSpan.innerText = 'Valid Password'
        passwordFeedbackSpan.classList.replace('error', 'success');
        validPassword = true;
        loginButton.disabled = !(validCallsign && validPassword);
    }
}