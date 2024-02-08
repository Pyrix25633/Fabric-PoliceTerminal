const table = document.getElementById('table');

export let pageFirstImg, pagePreviousImg;
export let pageInput, pageTimer, pageMaxSpan;
export let pageNextImg, pageLastImg;

export function initTable(headers) {
    const thead = document.createElement('thead');
    const theadRow = document.createElement('tr');
    for(const header of headers) {
        const th = document.createElement('th');
        th.innerText = header;
        theadRow.appendChild(th);
    }
    thead.appendChild(theadRow);
    table.appendChild(thead);
    const tbody = document.createElement('tbody');
    table.appendChild(tbody);
    const tfoot = document.createElement('tfoot');
    tfoot.innerHTML = `
        <tr>
            <td colspan="3">
                <div class="container">
                    <img src="./img/page-first.svg" alt="Page First Icon" class="button" id="page-first">
                    <img src="./img/page-previous.svg" alt="Page Previous Icon" class="button" id="page-previous">
                    <label for="page" id="page-label">Page</label>
                    <input type="text" id="page"> / <span id="page-max">8</span>
                    <img src="./img/page-next.svg" alt="Page Next Icon" class="button" id="page-next">
                    <img src="./img/page-last.svg" alt="Page Last Icon" class="button" id="page-last">
                </div>
            </td>
        </tr>
    `;
    table.appendChild(tfoot);
    pageFirstImg = document.getElementById('page-first');
    pagePreviousImg = document.getElementById('page-previous');
    pageInput = document.getElementById('page');
    pageMaxSpan = document.getElementById('page-max');
    pageNextImg = document.getElementById('page-next');
    pageLastImg = document.getElementById('page-last');
    return tbody;
}

export function setHandler(handler) {
    handler(0, handleFooter);

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
        handler(parseInt(pageInput.value), handleFooter);
    }
}

function handleFooter(page, pages) {
    let previousPage = page - 1;
    if(previousPage < 0) previousPage = 0;
    let lastPage = pages - 1;
    if(lastPage < 0) lastPage = 0;
    let nextPage = page + 1;
    if(nextPage > lastPage) nextPage = lastPage;

    pageFirstImg.addEventListener('click', () => {handler(0, handleFooter)});
    pagePreviousImg.addEventListener('click', () => {handler(previousPage, handleFooter)});
    pageInput.value = page + 1;
    pageMaxSpan.innerText = pages;
    pageNextImg.addEventListener('click', () => {handler(nextPage, handleFooter)});
    pageLastImg.addEventListener('click', () => {handler(lastPage, handleFooter)});
}