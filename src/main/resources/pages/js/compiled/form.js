var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
import { RequireNonNull } from './require-non-null.js';
export class Form {
    constructor(id, url, method, inputs, submitButton, success, statusCode) {
        this.valid = false;
        this.url = url;
        this.method = method;
        this.form = RequireNonNull.getElementById(id);
        this.inputs = inputs;
        for (const input of inputs)
            input.append(this);
        this.submitButton = submitButton;
        this.submitButton.append(this);
        this.submitButton.addClickListener(() => { this.submit(); });
        this.success = success;
        this.statusCode = statusCode;
    }
    appendChild(node) {
        this.form.appendChild(node);
    }
    validate() {
        this.valid = true;
        for (const input of this.inputs)
            this.valid = this.valid && !input.getError();
        this.submitButton.setDisabled(!this.valid);
    }
    submit() {
        return __awaiter(this, void 0, void 0, function* () {
            if (this.submitButton.isDisabled())
                return;
            const data = {};
            for (const input of this.inputs)
                data[input.id] = yield input.parse();
            $.ajax({
                url: this.url,
                method: this.method,
                data: JSON.stringify(data),
                contentType: 'application/json',
                success: this.success,
                statusCode: this.statusCode
            });
        });
    }
}
export class SubmitButton {
    constructor(text, iconSrc) {
        this.button = document.createElement('button');
        this.button.innerText = text;
        this.button.disabled = true;
        const icon = document.createElement('img');
        icon.classList.add('button');
        icon.src = iconSrc;
        icon.alt = text + ' Icon';
        this.button.appendChild(icon);
    }
    append(form) {
        const div = document.createElement('div');
        div.classList.add('container');
        div.appendChild(this.button);
        form.appendChild(div);
    }
    addClickListener(listener) {
        this.button.addEventListener('click', listener);
    }
    setDisabled(disabled) {
        this.button.disabled = disabled;
    }
    isDisabled() {
        return this.button.disabled;
    }
}
export class Input {
    constructor(id, type, labelText, feedbackText) {
        this.form = undefined;
        this.timeout = undefined;
        this.error = false;
        this.id = id;
        this.input = document.createElement('input');
        this.input.id = id;
        this.input.type = type;
        this.feedback = document.createElement('span');
        this.feedback.classList.add('text');
        this.feedback.innerText = feedbackText;
        this.labelText = labelText;
        this.input.addEventListener('keyup', () => {
            clearTimeout(this.timeout);
            this.timeout = setTimeout(() => {
                this.parse();
            }, 1000);
        });
        this.input.addEventListener('keydown', () => {
            clearTimeout(this.timeout);
        });
        this.input.addEventListener('focusout', () => {
            clearTimeout(this.timeout);
            this.parse();
        });
    }
    append(form) {
        this.form = form;
        const container = document.createElement('div');
        container.classList.add('container');
        const label = document.createElement('label');
        label.htmlFor = this.id;
        label.innerText = this.labelText;
        container.appendChild(label);
        container.appendChild(this.input);
        this.form.appendChild(container);
        this.form.appendChild(this.feedback);
        setTimeout(() => {
            if (this.input.value != '')
                this.parse();
        }, 250);
    }
    setError(error, feedbackText) {
        var _a;
        this.error = error;
        if (!this.feedback.classList.contains('error') && !this.feedback.classList.contains('success'))
            this.feedback.classList.add('error');
        if (this.error)
            this.feedback.classList.replace('success', 'error');
        else
            this.feedback.classList.replace('error', 'success');
        this.feedback.innerText = feedbackText;
        (_a = this.form) === null || _a === void 0 ? void 0 : _a.validate();
    }
    getError() {
        return this.error;
    }
}
export class ApiFeedbackInput extends Input {
    constructor(id, type, labelText, feedbackText, url, method) {
        super(id, type, labelText, feedbackText);
        this.url = url;
        this.method = method;
    }
    getInputValue() {
        return this.input.value;
    }
    parse() {
        return __awaiter(this, void 0, void 0, function* () {
            const data = {};
            data[this.id] = this.getInputValue();
            return new Promise((resolve) => {
                $.ajax({
                    url: this.url,
                    method: this.method,
                    data: data,
                    success: (res) => {
                        this.setError(res.feedback.includes('!'), res.feedback);
                        resolve(this.getInputValue());
                    },
                    error: (req, err) => {
                        console.error(err);
                        this.setError(true, 'Server unreachable!');
                        resolve(null);
                    }
                });
            });
        });
    }
}
