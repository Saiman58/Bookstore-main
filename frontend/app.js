/* ===================================================
   BOOKSTORE — APP.JS
   Все запросы идут на http://localhost:8081/api/...
   =================================================== */

const API = 'http://localhost:8081/api';

// ---------- STATE ----------
const state = {
  userId: null,
  username: null,
  currentReviewBookId: null,
  starRating: 0,
};

// ---------- TOAST ----------
function toast(msg, type = '') {
  const el = document.getElementById('toast');
  el.textContent = msg;
  el.className = `toast show ${type}`;
  setTimeout(() => el.classList.remove('show'), 3200);
}

// ---------- API HELPERS ----------
async function api(path, opts = {}) {
  try {
    const res = await fetch(API + path, {
      headers: { 'Content-Type': 'application/json' },
      ...opts,
    });
    if (!res.ok) {
      let msg = `Ошибка ${res.status}`;
      try { const j = await res.json(); msg = j.message || msg; } catch {}
      throw new Error(msg);
    }
    if (res.status === 204) return null;
    return res.json();
  } catch (e) {
    toast(e.message, 'error');
    throw e;
  }
}

// ---------- NAVIGATION ----------
document.querySelectorAll('.nav-btn').forEach(btn => {
  btn.addEventListener('click', () => {
    document.querySelectorAll('.nav-btn').forEach(b => b.classList.remove('active'));
    btn.classList.add('active');
    const page = btn.dataset.page;
    document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
    document.getElementById(`page-${page}`).classList.add('active');

    if (page === 'library') loadLibrary();
    if (page === 'stats')   loadStats();
    if (page === 'external-search') {
      // Очищаем предыдущий поиск при открытии
      document.getElementById('externalSearchInput').value = '';
      document.getElementById('externalBooksGrid').innerHTML = '';
    }
  });
});

// ---------- MODALS ----------
function openModal(id)  { document.getElementById(id).classList.add('open'); }
function closeModal(id) { document.getElementById(id).classList.remove('open'); }

document.querySelectorAll('.modal-close, [data-close]').forEach(btn => {
  btn.addEventListener('click', () => {
    const id = btn.dataset.close || btn.closest('.modal-backdrop').id;
    closeModal(id);
  });
});
document.querySelectorAll('.modal-backdrop').forEach(bd => {
  bd.addEventListener('click', e => {
    if (e.target === bd) closeModal(bd.id);
  });
});

// ---------- AUTH ----------
document.getElementById('btnLogin').addEventListener('click', () => openModal('modalLogin'));
document.getElementById('btnRegister').addEventListener('click', () => openModal('modalRegister'));
document.getElementById('linkToRegister').addEventListener('click', e => {
  e.preventDefault();
  closeModal('modalLogin');
  openModal('modalRegister');
});

// Войти по username (без JWT — ищем пользователя по имени)
document.getElementById('btnDoLogin').addEventListener('click', async () => {
  const username = document.getElementById('loginUsername').value.trim();
  if (!username) { toast('Введите username', 'error'); return; }
  try {
    const user = await api(`/users/username/${encodeURIComponent(username)}`);
    setCurrentUser(user);
    closeModal('modalLogin');
    toast(`Добро пожаловать, ${user.firstName || user.username}!`, 'success');
  } catch {}
});

// Регистрация
document.getElementById('btnDoRegister').addEventListener('click', async () => {
  const body = {
    username:  document.getElementById('regUsername').value.trim(),
    email:     document.getElementById('regEmail').value.trim(),
    password:  document.getElementById('regPassword').value,
    firstName: document.getElementById('regFirstName').value.trim(),
    lastName:  document.getElementById('regLastName').value.trim(),
  };
  if (!body.username || !body.email || !body.password) {
    toast('Заполните обязательные поля', 'error'); return;
  }
  try {
    const user = await api('/users/register', { method: 'POST', body: JSON.stringify(body) });
    setCurrentUser(user);
    closeModal('modalRegister');
    toast('Аккаунт создан! Добро пожаловать 🎉', 'success');
  } catch {}
});

function setCurrentUser(user) {
  state.userId   = user.id;
  state.username = user.username;
  const name = [user.firstName, user.lastName].filter(Boolean).join(' ') || user.username;
  document.getElementById('sidebarUserName').textContent = name;
  document.getElementById('sidebarUserMeta').textContent = `@${user.username}`;
  document.getElementById('userAvatar').textContent = (user.firstName || user.username)[0].toUpperCase();
  document.getElementById('btnLogin').style.display = 'none';
  document.getElementById('btnRegister').style.display = 'none';
}

// ---------- CATALOG ----------
document.getElementById('btnSearch').addEventListener('click', searchBooks);
document.getElementById('searchInput').addEventListener('keydown', e => { if (e.key === 'Enter') searchBooks(); });
document.getElementById('searchAuthor').addEventListener('keydown', e => { if (e.key === 'Enter') searchBooks(); });

// Загружаем все книги при старте
window.addEventListener('load', searchBooks);

async function searchBooks() {
  const title  = document.getElementById('searchInput').value.trim();
  const author = document.getElementById('searchAuthor').value.trim();

  let path = '/books/search';
  if (title)  path += `?title=${encodeURIComponent(title)}`;
  else if (author) path += `?author=${encodeURIComponent(author)}`;

  try {
    const books = await api(path);
    renderBooks(books);
  } catch {}
}

function renderBooks(books) {
  const grid = document.getElementById('booksGrid');
  if (!books.length) {
    grid.innerHTML = `<div class="empty-state"><span class="empty-icon">🔍</span><p>Ничего не найдено</p></div>`;
    return;
  }
  grid.innerHTML = books.map(book => `
    <div class="book-card" data-id="${book.id}" onclick="openBookDetail(${book.id})">
   <div class="book-card__cover">
       ${book.coverImageUrl
           ? `<img src="${book.coverImageUrl}" alt="${book.title}" style="width:100%; height:100%; object-fit:cover;" />`
           : '📗'}
   </div>
      <div class="book-card__title">${escHtml(book.title)}</div>
      <div class="book-card__author">${escHtml(book.author)}</div>
      ${book.pageCount ? `<span class="book-card__badge">${book.pageCount} стр.</span>` : ''}
    </div>
  `).join('');
}

// ---------- BOOK DETAIL ----------
async function openBookDetail(bookId) {
  try {
    const book = await api(`/books/${bookId}`);
    const el = document.getElementById('bookDetailContent');
    el.innerHTML = `
      <div class="book-detail__head">
        <div class="book-detail__cover">
          ${book.coverImageUrl
            ? `<img src="${escHtml(book.coverImageUrl)}" alt="${escHtml(book.title)}" />`
            : '📗'}
        </div>
        <div class="book-detail__meta">
          <h2>${escHtml(book.title)}</h2>
          <p>${escHtml(book.author)}</p>
          ${book.publisher    ? `<p>📌 ${escHtml(book.publisher)}</p>` : ''}
          ${book.pageCount    ? `<p>📄 ${book.pageCount} страниц</p>` : ''}
          ${book.publishedDate ? `<p>📅 ${book.publishedDate}</p>` : ''}
          ${book.isbn         ? `<p>ISBN: ${escHtml(book.isbn)}</p>` : ''}
        </div>
      </div>
      ${book.description ? `<div class="book-detail__desc">${escHtml(book.description)}</div>` : ''}
      <div class="book-detail__actions" id="detailActions">
        ${state.userId
          ? `<button class="btn-primary btn-sm" onclick="addToLibrary(${book.id}, 'WANT_TO_READ')">+ В библиотеку</button>
             <button class="btn-primary btn-sm" onclick="addToLibrary(${book.id}, 'READING')">Читаю сейчас</button>`
          : `<p style="color:var(--muted);font-size:13px">Войдите, чтобы добавить книгу в библиотеку</p>`}
        <button class="btn-danger btn-sm" onclick="deleteBook(${book.id})">Удалить из каталога</button>
      </div>
    `;
    openModal('modalBookDetail');
  } catch {}
}

async function addToLibrary(bookId, status) {
  if (!state.userId) { toast('Войдите в аккаунт', 'error'); return; }
  try {
    await api(`/users/${state.userId}/books`, {
      method: 'POST',
      body: JSON.stringify({ bookId, status }),
    });
    toast('Книга добавлена в библиотеку ✅', 'success');
    closeModal('modalBookDetail');
  } catch {}
}

async function deleteBook(bookId) {
  if (!confirm('Удалить книгу из каталога?')) return;
  try {
    await api(`/books/${bookId}`, { method: 'DELETE' });
    toast('Книга удалена', '');
    closeModal('modalBookDetail');
    searchBooks();
  } catch {}
}

// ---------- ADD BOOK ----------
document.getElementById('btnAddBook').addEventListener('click', () => openModal('modalAddBook'));

document.getElementById('btnDoAddBook').addEventListener('click', async () => {
  const body = {
    title:       document.getElementById('bookTitle').value.trim(),
    author:      document.getElementById('bookAuthor').value.trim(),
    description: document.getElementById('bookDescription').value.trim(),
    isbn:        document.getElementById('bookIsbn').value.trim(),
    pageCount:   parseInt(document.getElementById('bookPages').value) || null,
    publisher:   document.getElementById('bookPublisher').value.trim(),
  };
  if (!body.title || !body.author) { toast('Заполните название и автора', 'error'); return; }
  try {
    await api('/books', { method: 'POST', body: JSON.stringify(body) });
    toast('Книга добавлена в каталог ✅', 'success');
    closeModal('modalAddBook');
    // clear form
    ['bookTitle','bookAuthor','bookDescription','bookIsbn','bookPages','bookPublisher']
      .forEach(id => { document.getElementById(id).value = ''; });
    searchBooks();
  } catch {}
});

// ---------- LIBRARY ----------
document.getElementById('statusTabs').addEventListener('click', e => {
  const tab = e.target.closest('.tab');
  if (!tab) return;
  document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
  tab.classList.add('active');
  loadLibrary(tab.dataset.status);
});

async function loadLibrary(status = '') {
  if (!state.userId) {
    document.getElementById('libraryList').innerHTML = `
      <div class="empty-state"><span class="empty-icon">🔒</span><p>Войдите в аккаунт, чтобы видеть библиотеку</p></div>`;
    return;
  }
  try {
    const path = `/users/${state.userId}/books` + (status ? `?status=${status}` : '');
    const items = await api(path);
    renderLibrary(items);
  } catch {}
}

function renderLibrary(items) {
  const list = document.getElementById('libraryList');
  if (!items.length) {
    list.innerHTML = `<div class="empty-state"><span class="empty-icon">📕</span><p>Здесь пока пусто</p></div>`;
    return;
  }

  const statusLabels = {
    WANT_TO_READ: 'Хочу прочитать',
    READING:      'Читаю',
    READ:         'Прочитано',
    ABANDONED:    'Брошено',
  };

  list.innerHTML = items.map(ub => {
    const b = ub.book;
    const stars = renderStars(ub.userRating);
    return `
    <div class="lib-row" data-ubid="${ub.id}" data-bookid="${b.id}">
      <div class="lib-row__cover">
        ${b.coverImageUrl ? `<img src="${escHtml(b.coverImageUrl)}" alt="" />` : '📗'}
      </div>
      <div class="lib-row__info">
        <div class="lib-row__title">${escHtml(b.title)}</div>
        <div class="lib-row__author">${escHtml(b.author)}</div>
        <div class="lib-row__meta">
          ${ub.startedAt ? `Начато: ${fmtDate(ub.startedAt)}` : ''}
          ${ub.finishedAt ? ` · Закончено: ${fmtDate(ub.finishedAt)}` : ''}
        </div>
        ${ub.userReview ? `<div class="lib-row__meta" style="margin-top:4px;font-style:italic">"${escHtml(ub.userReview)}"</div>` : ''}
        <div class="stars" style="margin-top:4px">${stars}</div>
      </div>
      <div class="lib-row__actions">
        <select class="status-select" onchange="changeStatus(${b.id}, this.value)">
          ${Object.entries(statusLabels).map(([v,l]) =>
            `<option value="${v}" ${ub.status === v ? 'selected' : ''}>${l}</option>`
          ).join('')}
        </select>
        <button class="btn-primary btn-sm" onclick="openReview(${b.id})">★ Оценить</button>
        <button class="btn-danger btn-sm" onclick="removeFromLibrary(${b.id})">Удалить</button>
      </div>
    </div>`;
  }).join('');
}

function renderStars(rating) {
  if (!rating) return '';
  return Array.from({ length: 5 }, (_, i) =>
    `<span class="${i < rating ? 'star-filled' : 'star-empty'}">★</span>`
  ).join('');
}

async function changeStatus(bookId, status) {
  try {
    await api(`/users/${state.userId}/books/${bookId}/status?status=${status}`, { method: 'PATCH' });
    toast('Статус обновлён ✅', 'success');
    if (state.userId) loadStats();
  } catch {}
}

async function removeFromLibrary(bookId) {
  if (!confirm('Убрать книгу из библиотеки?')) return;
  try {
    await api(`/users/${state.userId}/books/${bookId}`, { method: 'DELETE' });
    toast('Книга убрана из библиотеки');
    const activeStatus = document.querySelector('.tab.active')?.dataset.status || '';
    loadLibrary(activeStatus);
  } catch {}
}

// ---------- REVIEW ----------
function openReview(bookId) {
  state.currentReviewBookId = bookId;
  state.starRating = 0;
  document.getElementById('reviewText').value = '';
  document.querySelectorAll('#starsInput .star').forEach(s => s.classList.remove('active'));
  openModal('modalReview');
}

document.querySelectorAll('#starsInput .star').forEach(star => {
  star.addEventListener('click', () => {
    const v = parseInt(star.dataset.v);
    state.starRating = v;
    document.querySelectorAll('#starsInput .star').forEach(s => {
      s.classList.toggle('active', parseInt(s.dataset.v) <= v);
    });
  });
});

document.getElementById('btnDoReview').addEventListener('click', async () => {
  const review = document.getElementById('reviewText').value.trim();
  const rating = state.starRating;
  if (!rating) { toast('Поставьте оценку', 'error'); return; }
  try {
    await api(`/users/${state.userId}/books/${state.currentReviewBookId}/review`, {
      method: 'POST',
      body: JSON.stringify({ rating, review }),
    });
    toast('Отзыв сохранён ✅', 'success');
    closeModal('modalReview');
    const activeStatus = document.querySelector('.tab.active')?.dataset.status || '';
    loadLibrary(activeStatus);
  } catch {}
});

// ---------- STATS ----------
async function loadStats() {
  if (!state.userId) return;
  try {
    const stats = await api(`/users/${state.userId}/books/stats`);
    document.getElementById('stat-want').textContent     = stats.wantToRead ?? 0;
    document.getElementById('stat-reading').textContent  = stats.reading ?? 0;
    document.getElementById('stat-read').textContent     = stats.read ?? 0;
    document.getElementById('stat-abandoned').textContent = stats.abandoned ?? 0;
    drawChart(stats);
  } catch {}
}

function drawChart(stats) {
  const canvas = document.getElementById('statsChart');
  const empty  = document.getElementById('statsEmpty');
  const total  = (stats.wantToRead || 0) + (stats.reading || 0) + (stats.read || 0) + (stats.abandoned || 0);
  if (!total) { empty.style.display = 'flex'; canvas.style.display = 'none'; return; }

  empty.style.display = 'none';
  canvas.style.display = 'block';
  const ctx = canvas.getContext('2d');
  const W = canvas.width, H = canvas.height;
  ctx.clearRect(0, 0, W, H);

  const data = [
    { label: 'Хочу прочитать', value: stats.wantToRead || 0, color: '#d4a04a' },
    { label: 'Читаю',          value: stats.reading    || 0, color: '#8b5e3c' },
    { label: 'Прочитано',      value: stats.read       || 0, color: '#5a7a5a' },
    { label: 'Брошено',        value: stats.abandoned  || 0, color: '#c44f2a' },
  ].filter(d => d.value > 0);

  // Simple bar chart
  const maxV = Math.max(...data.map(d => d.value));
  const barW  = 60;
  const gap   = 36;
  const padX  = 60;
  const padY  = 20;
  const chartH = H - padY * 2 - 36; // 36 for labels
  const totalW = data.length * (barW + gap) - gap;
  const startX = (W - totalW) / 2;

  ctx.font = '13px DM Sans, sans-serif';
  ctx.textAlign = 'center';

  data.forEach((d, i) => {
    const x = startX + i * (barW + gap);
    const barH = (d.value / maxV) * chartH;
    const y = padY + chartH - barH;

    // Bar
    ctx.fillStyle = d.color;
    ctx.beginPath();
    ctx.roundRect(x, y, barW, barH, [6, 6, 0, 0]);
    ctx.fill();

    // Value on top
    ctx.fillStyle = '#1a1410';
    ctx.font = 'bold 16px DM Sans, sans-serif';
    ctx.fillText(d.value, x + barW / 2, y - 6);

    // Label
    ctx.fillStyle = '#9a8a78';
    ctx.font = '12px DM Sans, sans-serif';
    ctx.fillText(d.label, x + barW / 2, H - 6);
  });
}

// ---------- UTILS ----------
function escHtml(str) {
  if (!str) return '';
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}

function fmtDate(iso) {
  if (!iso) return '';
  return new Date(iso).toLocaleDateString('ru-RU', { day: '2-digit', month: 'short', year: 'numeric' });
}

// ---------- EXTERNAL SEARCH (Open Library) ----------

// Переменная для хранения выбранной книги для импорта
let selectedBookForImport = null;

// Ждём полной загрузки DOM
document.addEventListener('DOMContentLoaded', function() {
    const searchBtn = document.getElementById('btnExternalSearch');
    const searchInput = document.getElementById('externalSearchInput');

    console.log('DOM загружен, ищу кнопку...');

    if (searchBtn) {
        console.log('✅ Кнопка btnExternalSearch найдена!');
        searchBtn.addEventListener('click', function() {
            console.log('🔘 Кнопка нажата!');
            searchExternalBooks();
        });
    } else {
        console.error('❌ Кнопка btnExternalSearch НЕ найдена!');
    }

    if (searchInput) {
        console.log('✅ Поле ввода найдено');
        searchInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                console.log('⌨️ Enter нажат!');
                searchExternalBooks();
            }
        });
    } else {
        console.error('❌ Поле externalSearchInput НЕ найдено!');
    }
});

async function searchExternalBooks() {
    console.log('🔍 searchExternalBooks вызвана!');

    const searchInput = document.getElementById('externalSearchInput');
    const query = searchInput ? searchInput.value.trim() : '';
    const searchTypeRadio = document.querySelector('input[name="searchType"]:checked');
    const searchType = searchTypeRadio ? searchTypeRadio.value : 'title';

    console.log('📝 Запрос:', query, 'Тип поиска:', searchType);

    if (!query) {
        toast('Введите название или автора', 'error');
        return;
    }

    const grid = document.getElementById('externalBooksGrid');
    if (!grid) {
        console.error('❌ Grid не найден!');
        return;
    }

    grid.innerHTML = `<div class="empty-state"><span class="empty-icon">⏳</span><p>Поиск...</p></div>`;

    try {
        const url = `${API}/books/external/search?${searchType}=${encodeURIComponent(query)}`;
        console.log('🌐 URL запроса:', url);

        const response = await fetch(url);
        console.log('📡 Статус ответа:', response.status);

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}`);
        }

        const books = await response.json();
        console.log('📚 Найдено книг:', books.length);
        renderExternalBooks(books);
    } catch (error) {
        console.error('❌ Ошибка:', error);
        grid.innerHTML = `<div class="empty-state"><span class="empty-icon">❌</span><p>Ошибка поиска: ${error.message}</p></div>`;
        toast('Ошибка подключения к серверу', 'error');
    }
}

function renderExternalBooks(books) {
    console.log('🎨 renderExternalBooks вызвана, книг:', books.length);

    const grid = document.getElementById('externalBooksGrid');

    if (!books || books.length === 0) {
        grid.innerHTML = `<div class="empty-state"><span class="empty-icon">📭</span><p>Ничего не найдено</p></div>`;
        return;
    }

    grid.innerHTML = books.map(book => `
        <div class="book-card">
            <div class="book-card__cover">📗</div>
            <div class="book-card__title">${escapeHtml(book.title) || 'Без названия'}</div>
            <div class="book-card__author">${escapeHtml(book.author) || 'Неизвестный автор'}</div>
            ${book.pageCount ? `<div class="book-card__badge">${book.pageCount} стр.</div>` : ''}
            ${book.isbn ? `<div class="book-card__badge" style="background: var(--sage);">ISBN: ${book.isbn}</div>` : ''}
            <button class="btn-import" onclick="window.showImportModal('${escapeHtml(book.title || '')}', '${escapeHtml(book.author || '')}', '${book.isbn || ''}')">
                📥 Импортировать
            </button>
        </div>
    `).join('');

    console.log('✅ Отображено книг:', books.length);
}

function showImportModal(title, author, isbn) {
    console.log('📥 showImportModal вызвана:', title);

    selectedBookForImport = { title, author, isbn };

    const preview = document.getElementById('importBookPreview');
    if (preview) {
        preview.innerHTML = `
            <div class="import-preview">
                <div class="import-preview__cover">📗</div>
                <div class="import-preview__info">
                    <div class="import-preview__title">${escapeHtml(title)}</div>
                    <div class="import-preview__author">${escapeHtml(author)}</div>
                    ${isbn ? `<div class="import-preview__details">ISBN: ${isbn}</div>` : ''}
                    <div class="import-preview__details">Книга будет добавлена в каталог</div>
                </div>
            </div>
        `;
    }

    openModal('modalImportConfirm');
}

// Обработчик подтверждения импорта
const confirmBtn = document.getElementById('btnConfirmImport');
if (confirmBtn) {
    confirmBtn.addEventListener('click', async () => {
        if (!selectedBookForImport) return;

        closeModal('modalImportConfirm');
        toast('Импорт книги...', '');

        try {
            if (selectedBookForImport.isbn) {
                const response = await fetch(`${API}/books/external/import/${selectedBookForImport.isbn}`);
                if (!response.ok) throw new Error('Ошибка импорта');
                const book = await response.json();
                toast(`Книга "${book.title}" успешно импортирована!`, 'success');
            } else {
                const response = await fetch(`${API}/books`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        title: selectedBookForImport.title,
                        author: selectedBookForImport.author,
                        isbn: selectedBookForImport.isbn || null
                    })
                });
                if (!response.ok) throw new Error('Ошибка создания');
                const book = await response.json();
                toast(`Книга "${book.title}" добавлена в каталог!`, 'success');
            }

            // Очищаем поиск
            const searchInput = document.getElementById('externalSearchInput');
            if (searchInput) searchInput.value = '';
            const grid = document.getElementById('externalBooksGrid');
            if (grid) grid.innerHTML = '';
            searchBooks(); // Обновляем основной каталог

        } catch (error) {
            toast(`Ошибка импорта: ${error.message}`, 'error');
        }

        selectedBookForImport = null;
    });
}

// Функция для экранирования HTML
function escapeHtml(str) {
    if (!str) return '';
    return String(str).replace(/[&<>]/g, function(m) {
        if (m === '&') return '&amp;';
        if (m === '<') return '&lt;';
        if (m === '>') return '&gt;';
        return m;
    });
}

// ========== РЕГИСТРИРУЕМ ГЛОБАЛЬНЫЕ ФУНКЦИИ ==========
// ЭТО САМОЕ ВАЖНОЕ - ДОБАВЬТЕ ЭТОТ БЛОК!

window.searchExternalBooks = searchExternalBooks;
window.renderExternalBooks = renderExternalBooks;
window.showImportModal = showImportModal;

console.log('✅ Все функции зарегистрированы глобально!');
console.log('📌 searchExternalBooks доступна:', typeof window.searchExternalBooks);
console.log('📌 renderExternalBooks доступна:', typeof window.renderExternalBooks);
console.log('📌 showImportModal доступна:', typeof window.showImportModal);
