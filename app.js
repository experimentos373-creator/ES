// Estado Inicial / Dados por defeito
const DEFAULT_DATA = {
    matches: [
        { id: 1, date: '2026-06-10', time: '18:00', stadium: 'Estádio da Luz', homeTeam: 'Portugal', awayTeam: 'Espanha', phase: 'Grupos', status: 'Agendado', referees: [], events: [] },
        { id: 2, date: '2026-06-11', time: '20:00', stadium: 'Estádio Alvalade', homeTeam: 'Brasil', awayTeam: 'França', phase: 'Grupos', status: 'Agendado', referees: [], events: [] },
        { id: 3, date: '2026-06-12', time: '18:00', stadium: 'Estádio do Dragão', homeTeam: 'Argentina', awayTeam: 'Inglaterra', phase: 'Grupos', status: 'Agendado', referees: [], events: [] },
        { id: 4, date: '2026-07-01', time: '18:00', stadium: 'Estádio da Luz', homeTeam: 'Portugal', awayTeam: 'França', phase: 'Oitavos', status: 'Agendado', referees: [], events: [] },
        { id: 5, date: '2026-07-02', time: '21:00', stadium: 'Alvalade', homeTeam: 'Espanha', awayTeam: 'Brasil', phase: 'Oitavos', status: 'Agendado', referees: [], events: [] },
        { id: 6, date: '2026-07-08', time: '19:00', stadium: 'Camp Nou', homeTeam: 'Portugal', awayTeam: 'Brasil', phase: 'Quartos', status: 'Agendado', referees: [], events: [] },
        { id: 7, date: '2026-07-09', time: '19:00', stadium: 'Wembley', homeTeam: 'Argentina', awayTeam: 'França', phase: 'Quartos', status: 'Agendado', referees: [], events: [] },
        { id: 8, date: '2026-07-14', time: '20:00', stadium: 'Estádio da Luz', homeTeam: 'Portugal', awayTeam: 'Argentina', phase: 'Meias-Finais', status: 'Agendado', referees: [], events: [] },
        { id: 9, date: '2026-07-15', time: '20:00', stadium: 'Santiago Bernabéu', homeTeam: 'Brasil', awayTeam: 'França', phase: 'Meias-Finais', status: 'Agendado', referees: [], events: [] },
        { id: 10, date: '2026-07-19', time: '21:00', stadium: 'Santiago Bernabéu', homeTeam: 'Brasil', awayTeam: 'Alemanha', phase: 'Final', status: 'Agendado', referees: [], events: [] }
    ],
    teams: [
        { name: 'Portugal', flag: '🇵🇹', group: 'A', coach: 'Roberto Martínez', players: [
            { name: 'Cristiano Ronaldo', number: 7, position: 'Avançado' },
            { name: 'Bruno Fernandes', number: 8, position: 'Médio' }
        ] },
        { name: 'Espanha', flag: '🇪🇸', group: 'A', coach: 'Luis de la Fuente', players: [] },
        { name: 'Brasil', flag: '🇧🇷', group: 'B', coach: 'Dorival Júnior', players: [] },
        { name: 'França', flag: '🇫🇷', group: 'B', coach: 'Didier Deschamps', players: [] }
    ],
    referees: [
        { id: 1, email: 'ref1@fifa.com', name: 'Artur Soares Dias', nationality: 'Portugal', type: 'Principal', status: 'Disponível', matchesCount: 2, lastMatchDate: '2026-06-10' },
        { id: 2, email: 'ref2@fifa.com', name: 'Szymon Marciniak', nationality: 'Polónia', type: 'Principal', status: 'Disponível', matchesCount: 3, lastMatchDate: '2026-06-08' },
        { id: 3, email: 'ref3@fifa.com', name: 'Daniele Orsato', nationality: 'Itália', type: 'Principal', status: 'Disponível', matchesCount: 1, lastMatchDate: '2026-06-05' },
        { id: 4, email: 'ref4@fifa.com', name: 'Clément Turpin', nationality: 'França', type: 'Principal', status: 'Descanso', matchesCount: 2, lastMatchDate: '2026-06-15' },
        { id: 5, email: 'ref5@fifa.com', name: 'Facundo Tello', nationality: 'Argentina', type: 'Principal', status: 'Disponível', matchesCount: 0, lastMatchDate: null },
        { id: 6, email: 'ref6@fifa.com', name: 'Wilton Sampaio', nationality: 'Brasil', type: 'VAR', status: 'Disponível', matchesCount: 4, lastMatchDate: '2026-06-12' },
        { id: 7, email: 'ref7@fifa.com', name: 'Massimiliano Irrati', nationality: 'Itália', type: 'VAR', status: 'Disponível', matchesCount: 5, lastMatchDate: '2026-06-10' },
        { id: 8, email: 'ref8@fifa.com', name: 'Tiago Martins', nationality: 'Portugal', type: 'VAR', status: 'Descanso', matchesCount: 2, lastMatchDate: '2026-06-16' },
        { id: 9, email: 'ref9@fifa.com', name: 'Pau Cebrián Devís', nationality: 'Espanha', type: 'Assistente', status: 'Disponível', matchesCount: 3, lastMatchDate: '2026-06-11' },
        { id: 10, email: 'ref10@fifa.com', name: 'Nicolas Danos', nationality: 'França', type: 'Assistente', status: 'Disponível', matchesCount: 2, lastMatchDate: '2026-06-09' }
    ],
    // ... rest remains similar in structure but we'll manage it via helper functions
    tickets: { sold: 45000, available: 15000, categories: [
        { id: 'VIP', price: 500, sold: 1000, capacity: 1200, status: 'Aberto' },
        { id: 'Geral', price: 80, sold: 44000, capacity: 50000, status: 'Aberto' }
    ]},
    hotels: [
        { id: 1, name: 'Hotel Mundial', location: 'Lisboa', capacity: 100, team: 'Portugal', status: 'Ocupado' }
    ],
    users: [
        { email: 'admin@fifa.com', role: 'Administrador', name: 'Super Admin' },
        { email: 'arb@fifa.com', role: 'Gestor de Arbitragem', name: 'Chefe Árbitro' },
        { email: 'team@fpf.pt', role: 'Gestor de Equipa', name: 'Manager Portugal', team: 'Portugal' },
        { email: 'log@fifa.com', role: 'Gestor de Logística', name: 'Chefe Logística' },
        { email: 'ref@fifa.com', role: 'Árbitro', name: 'Artur Dias' }
    ],
    logs: []
};

// ... initDB, getData, saveData functions ...

// Funções para Árbitros
function addMatchEvent(matchId, type, minute, player) {
    const data = getData();
    const match = data.matches.find(m => m.id === matchId);
    if (match) {
        match.events.push({ type, minute, player, timestamp: new Date().toISOString() });
        saveData(data);
        return true;
    }
    return false;
}

function toggleRefereeAvailability(email, status) {
    const data = getData();
    const referee = data.referees.find(r => r.email === email);
    if (referee) {
        referee.status = status;
        saveData(data);
        return true;
    }
    return false;
}

// Inicialização do LocalStorage
function initDB() {
    const stored = localStorage.getItem('wc_data');
    if (!stored) {
        localStorage.setItem('wc_data', JSON.stringify(DEFAULT_DATA));
    } else {
        // Se adicionarmos novos jogos por defeito, atualizar o DB
        const currentData = JSON.parse(stored);
        if (currentData.matches.length < DEFAULT_DATA.matches.length) {
            localStorage.setItem('wc_data', JSON.stringify(DEFAULT_DATA));
            console.log('Base de dados atualizada com novos jogos.');
        }
    }
}

function getData() {
    return JSON.parse(localStorage.getItem('wc_data'));
}

function saveData(data) {
    localStorage.setItem('wc_data', JSON.stringify(data));
}

// Lógica de Autenticação / Roles
function getCurrentUser() {
    return JSON.parse(sessionStorage.getItem('wc_current_user')) || { role: 'Público' };
}

function login(email) {
    const data = getData();
    const user = data.users.find(u => u.email === email);
    if (user) {
        sessionStorage.setItem('wc_current_user', JSON.stringify(user));
        return true;
    }
    return false;
}

// Inserir Role Switcher em todas as páginas
function injectRoleSwitcher() {
    const switcher = document.createElement('div');
    switcher.id = 'role-switcher';
    switcher.innerHTML = `
        <p style="font-size: 0.8rem; margin-bottom: 5px; color: #666;"><strong>Role Switcher (Protótipo)</strong></p>
        <select id="role-select" style="padding: 5px; width: 100%;">
            <option value="Público">Público</option>
            <option value="Árbitro">Árbitro</option>
            <option value="Gestor de Arbitragem">Gestor Árbitros</option>
            <option value="Gestor de Equipa">Gestor Equipa</option>
            <option value="Gestor de Logística">Gestor Logística</option>
            <option value="Administrador">Administrador</option>
        </select>
        <button onclick="changeRole()" style="margin-top: 5px; width: 100%; background: #00D26A; border: none; color: white; padding: 5px; border-radius: 4px; cursor: pointer;">Mudar Vista</button>
    `;
    document.body.appendChild(switcher);

    const user = getCurrentUser();
    document.getElementById('role-select').value = user.role;
}

function changeRole() {
    const newRole = document.getElementById('role-select').value;
    const data = getData();
    let user;

    if (newRole === 'Público') {
        sessionStorage.removeItem('wc_current_user');
        window.location.href = 'publico.html';
        return;
    }

    user = data.users.find(u => u.role === newRole);
    sessionStorage.setItem('wc_current_user', JSON.stringify(user));

    const routes = {
        'Árbitro': 'arbitro.html',
        'Gestor de Arbitragem': 'gestor_arbitragem.html',
        'Gestor de Equipa': 'gestor_equipa.html',
        'Gestor de Logística': 'gestor_logistica.html',
        'Administrador': 'admin.html'
    };

    window.location.href = routes[newRole] || 'index.html';
}



// Inicializar na carga da página
document.addEventListener('DOMContentLoaded', () => {
    initDB();
    injectRoleSwitcher();
});
