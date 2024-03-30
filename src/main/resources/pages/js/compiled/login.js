var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
import { ApiFeedbackInput, Form, Input, SubmitButton } from './form.js';
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
        super('password', 'password', 'Password:', 'Input Password');
    }
    parse() {
        return __awaiter(this, void 0, void 0, function* () {
            if (this.input.value.length != 8) {
                this.setError(true, '8 Characters needed!');
                return;
            }
            let digits = 0, symbols = 0;
            for (let i = 0; i < this.input.value.length; i++) {
                const c = this.input.value.codePointAt(i);
                if (c == undefined)
                    break;
                if (c >= 48 && c <= 57)
                    digits++;
                else if ((c >= 45 && c <= 47) || c == 35 || c == 64 || c == 42 || c == 95)
                    symbols++;
                else if (!((c >= 97 && c <= 122) || (c >= 65 && c <= 90))) {
                    this.setError(true, 'Invalid Character: ' + String.fromCodePoint(c) + '!');
                    return;
                }
            }
            if (digits < 1 || symbols < 1) {
                this.setError(true, 'At least 1 Digit and 1 Symbol needed!');
                return;
            }
            this.setError(false, 'Valid Password');
            return this.input.value;
        });
    }
}
const callsignInput = new CallsignInput();
const passwordInput = new PasswordInput();
class LoginForm extends Form {
    constructor() {
        super('login-form', '/api/auth/login', 'POST', [
            callsignInput, passwordInput
        ], new LoginButton(), () => {
            window.location.href = '/';
        }, {
            400: () => {
                console.error('400: Bad Request');
            },
            401: () => {
                passwordInput.setError(true, 'Wrong Password!');
            },
            404: () => {
                callsignInput.setError(true, 'Callsign not found!');
            },
            500: () => {
                console.error('500: Internal Server Error');
            }
        });
    }
}
const loginForm = new LoginForm();
