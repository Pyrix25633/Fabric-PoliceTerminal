import { RequireNonNull } from "./utils.js";

class Tabs {
    private readonly nav: HTMLElement;
    private readonly tabs: Tab[];

    constructor(tabs: Tab[]) {
        this.nav = RequireNonNull.getElementById('tabs');
        this.tabs = tabs;
        const current: string = RequireNonNull.parse(/^.*(\/.*)$/.exec(location.href))[1];
        for(const tab of tabs)
            tab.appendTo(this, current == tab.url);
    }

    appendChild(node: HTMLElement): void {
        this.nav.appendChild(node);
    }
}

class Tab {
    readonly url: string;
    private readonly title: string;

    constructor(url: string, title: string) {
        this.url = url;
        this.title = title;
    }

    appendTo(tabs: Tabs, current: boolean): void {
        const div = document.createElement('div');
        div.classList.add('tab', 'container');
        if(current) div.classList.add('active');
        else div.addEventListener('click', () => {
            location.href = this.url;
        });
        const span = document.createElement('span');
        span.classList.add('title');
        span.innerText = this.title;
        div.appendChild(span);
        const img = document.createElement('img');
        img.classList.add('icon');
        img.src = '/img' + this.url + '.svg';
        img.alt = this.title + ' Icon';
        div.appendChild(img);
        tabs.appendChild(div);
    }
}

class IndexTab extends Tab {
    constructor() {
        super('/', '');
    }

    appendTo(tabs: Tabs, current: boolean): void {
        const div = document.createElement('div');
        div.classList.add('tab', 'container');
        if(current) div.classList.add('active');
        else div.addEventListener('click', () => {
            location.href = '/';
        });
        const police = document.createElement('span');
        police.classList.add('title', 'police-color');
        police.innerText = 'POLICE';
        div.appendChild(police);
        const terminal = document.createElement('span');
        terminal.classList.add('title');
        terminal.innerText = 'TERMINAL';
        div.appendChild(terminal);
        const img = document.createElement('img');
        img.classList.add('icon');
        img.src = '/img/icon.svg';
        img.alt = 'Police Terminal Icon';
        div.appendChild(img);
        tabs.appendChild(div);
    }
}

const tabs = new Tabs([
    new IndexTab(),
    new Tab('/citizens', 'Citizens'),
    new Tab('/officers', 'Officers'),
    new Tab('/emergency-calls', 'Emergency Calls'),
    new Tab('/incidents', 'Incidents'),
    new Tab('/manual', 'Manual'),
]);