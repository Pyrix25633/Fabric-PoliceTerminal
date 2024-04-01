import { ApiFeedbackInput, Form, Input, SubmitButton } from './form.js';
import { defaultStatusCode } from './utils.js';

class LoginButton extends SubmitButton {
    constructor() {
        super('Login', './img/login.svg');
    }
}

class CallsignInput extends ApiFeedbackInput {
    constructor() {
        super('callsign', 'text', 'Callsign:', 'Input Callsign', 'api/auth/callsign-login-feedback', 'GET');
    }
}

class PasswordInput extends Input {
    constructor() {
        super('password', 'password', 'Password:', 'Input Password')
    }

    async parse(): Promise<string | void> {
        if(this.input.value.length != 8) {
            this.setError(true, '8 Characters needed!')
            return;
        }
        let digits = 0, symbols = 0;
        for(let i = 0; i < this.input.value.length; i++) {
            const c: number | undefined = this.input.value.codePointAt(i);
            if(c == undefined) break;
            if(c >= 48 && c <= 57) digits++;
            else if((c >= 45 && c <= 47) || c == 35 || c == 64 || c == 42 || c == 95) symbols++;
            else if(!((c >= 97 && c <= 122) || (c >= 65 && c <= 90))) {
                this.setError(true, 'Invalid Character: ' + String.fromCodePoint(c) + '!');
                return;
            }
        }
        if(digits < 1 || symbols < 1) {
            this.setError(true, 'At least 1 Digit and 1 Symbol needed!');
            return;
        }
        this.setError(false, 'Valid Password');
        return this.input.value;
    }
}

const callsignInput = new CallsignInput();
const passwordInput = new PasswordInput();

const loginStatusCode = Object.assign({}, defaultStatusCode);
loginStatusCode[401] = (): void => {
    passwordInput.setError(true, 'Wrong Password!');
};
loginStatusCode[404] = (): void => {
    callsignInput.setError(true, 'Callsign not found!');
};

class LoginForm extends Form {
    constructor() {
        super('login-form', '/api/auth/login', 'POST', [
            callsignInput, passwordInput
        ], new LoginButton(), (): void => {
            window.location.href = '/';
        }, loginStatusCode);
    }
}

const loginForm = new LoginForm();