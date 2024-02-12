const table = document.getElementById('table');

let pageFirstImg, pagePreviousImg;
let pageInput, pageTimer, pageMaxSpan;
let pageNextImg, pageLastImg;
let page;
let order;
let orderImgs = [];
let handler;
let firstListener, previousListener, nextListener, lastListener;

export function initTable(groups, headers) {
    const thead = document.createElement('thead');
    if(groups != null) {
        const groupsTr = document.createElement('tr');
        for(const group of groups) {
            const th = document.createElement('th');
            if(group.text != undefined) {
                th.innerText = group.text;
                th.colSpan = group.colspan;
                th.classList.add('group');
            }
            groupsTr.appendChild(th);
        }
        thead.appendChild(groupsTr);
    }
    
    order = {field: headers[0].order, ascending: true};
    const headersTr = document.createElement('tr');
    for(const header of headers) {
        const th = document.createElement('th');
        if(header.order != undefined) {
            const div = document.createElement('div');
            div.classList.add('container');
            const span = document.createElement('span');
            span.classList.add('th');
            span.innerText = header.text;
            const img = document.createElement('img');
            img.classList.add('button');
            img.alt = 'Order Icon';
            img.id = 'order-' + header.order;
            orderImgs = orderImgs.concat(img);
            div.appendChild(span);
            div.appendChild(img);
            th.appendChild(div);
        }
        else {
            th.innerText = header.text;
        }
        headersTr.appendChild(th);
    }
    handleHeader(order);
    thead.appendChild(headersTr);
    table.appendChild(thead);

    const tbody = document.createElement('tbody');
    table.appendChild(tbody);
    const tfoot = document.createElement('tfoot');
    const footerTr = document.createElement('tr');
    const footerTd = document.createElement('td');
    footerTd.colSpan = headers.length;
    footerTd.innerHTML = `
        <div class="container">
            <img src="./img/page-first.svg" alt="Page First Icon" class="button" id="page-first">
            <img src="./img/page-previous.svg" alt="Page Previous Icon" class="button" id="page-previous">
            <label for="page" id="page-label">Page</label>
            <input type="text" id="page"> / <span id="page-max">8</span>
            <img src="./img/page-next.svg" alt="Page Next Icon" class="button" id="page-next">
            <img src="./img/page-last.svg" alt="Page Last Icon" class="button" id="page-last">
        </div>
    `;
    footerTr.appendChild(footerTd);
    tfoot.appendChild(footerTr);
    table.appendChild(tfoot);
    pageFirstImg = document.getElementById('page-first');
    pagePreviousImg = document.getElementById('page-previous');
    pageInput = document.getElementById('page');
    pageMaxSpan = document.getElementById('page-max');
    pageNextImg = document.getElementById('page-next');
    pageLastImg = document.getElementById('page-last');
    return tbody;
}

export function setHandler(h) {
    handler = h;
    handler(0, order, handleHeader, handleFooter);

    for(const img of orderImgs) {
        let field = img.id.replace('order-', '');
        img.addEventListener('click', () => {
            let ascending = true;
            if(order.field == field) ascending = !order.ascending;
            order = {field: field, ascending: ascending};
            handler(page, order, handleHeader, handleFooter);
        });
    }

    pageInput.addEventListener('keyup', () => {
        clearTimeout(pageTimer);
        pageTimer = setTimeout(pageTyped, 1000);
    });
    pageInput.addEventListener('keydown', () => {
        clearTimeout(pageTimer);
    });
    pageInput.addEventListener('focusout', () => {
        clearTimeout(pageTimer);
        pageTyped();
    });
    function pageTyped() {
        handler(parseInt(pageInput.value) - 1, handleFooter);
    }
}

function handleHeader(order) {
    for(const img of orderImgs) {
        if(img.id == 'order-' + order.field)
            img.src = './img/order-' + (order.ascending ? 'a' : 'de') + 'scending.svg';
        else
            img.src = './img/order.svg';
    }
}

function handleFooter(p, pages) {
    page = p;
    let previousPage = page - 1;
    if(previousPage < 0) previousPage = 0;
    let lastPage = pages - 1;
    if(lastPage < 0) lastPage = 0;
    let nextPage = page + 1;
    if(nextPage > lastPage) nextPage = lastPage;

    pageFirstImg.removeEventListener('click', firstListener);
    pagePreviousImg.removeEventListener('click', previousListener);
    pageNextImg.removeEventListener('click', nextListener);
    pageLastImg.removeEventListener('click', lastListener);

    firstListener = () => {handler(0, order, handleHeader, handleFooter)};
    previousListener = () => {handler(previousPage, order, handleHeader, handleFooter)}
    nextListener = () => {handler(nextPage, order, handleHeader, handleFooter)};
    lastListener = () => {handler(lastPage, order, handleHeader, handleFooter)};

    pageFirstImg.addEventListener('click', firstListener);
    pagePreviousImg.addEventListener('click', previousListener);
    pageInput.value = page + 1;
    pageMaxSpan.innerText = pages;
    pageNextImg.addEventListener('click', nextListener);
    pageLastImg.addEventListener('click', lastListener);
    // Fix for weird FireFox rendering bug
    $('#table').hide();
    setTimeout(function() {
        $('#table').show();
    }, 0);
}