const STADIUMS_LIST = ['Estádio da Luz','Estádio Alvalade','Estádio do Dragão','Camp Nou','Wembley','Santiago Bernabéu','Azteca','Rose Bowl'];
const PHASES_LIST = ['Grupos','Dezasseis-avos','Oitavos','Quartos','Meias-Finais','Final'];
const DEFAULT_TICKETS = [
    {category:'Premium',price:200,sold:120,total:500},
    {category:'Intermediária',price:150,sold:450,total:1000},
    {category:'Económica',price:100,sold:1200,total:5000},
    {category:'Local',price:50,sold:3500,total:10000}
];

function addMatch(matchData) {
    const data = getData();
    const phase = matchData.phase;
    const id = data.matches.length > 0 ? Math.max(...data.matches.map(m => m.id)) + 1 : 1;
    const match = {
        id, date: matchData.date, time: matchData.time, stadium: matchData.stadium,
        homeTeam: matchData.homeTeam, awayTeam: matchData.awayTeam, phase: phase,
        status: 'Agendado', winner: null, evaluated: false,
        refPrincipal: null, refAssistente1: null, refVAR: null, refQuarto: null,
        events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS))
    };
    data.matches.push(match);
    saveData(data);
    return { success: true, match };
}

function finalizeMatch(matchId, winnerTeam) {
    const data = getData();
    const match = data.matches.find(m => m.id === matchId);
    if (!match) return false;
    match.status = 'Finalizado';
    match.winner = winnerTeam;
    const phaseOrder = ['Dezasseis-avos','Oitavos','Quartos','Meias-Finais','Final'];
    const currentIdx = phaseOrder.indexOf(match.phase);
    if (currentIdx < phaseOrder.length - 1) {
        const nextPhase = phaseOrder[currentIdx + 1];
        const samePhaseMatches = data.matches.filter(m => m.phase === match.phase).sort((a,b) => a.id - b.id);
        const matchIndex = samePhaseMatches.findIndex(m => m.id === matchId);
        const pairIndex = Math.floor(matchIndex / 2);
        const nextMatches = data.matches.filter(m => m.phase === nextPhase).sort((a,b) => a.id - b.id);
        if (nextMatches[pairIndex]) {
            const slot = (matchIndex % 2 === 0) ? 'homeTeam' : 'awayTeam';
            nextMatches[pairIndex][slot] = winnerTeam;
        }
    }
    saveData(data);
    return true;
}

function addTeam(teamData) {
    const data = getData();
    if (data.teams.find(t => t.name === teamData.name)) return { success: false, msg: 'Equipa já existe.' };
    data.teams.push({ name: teamData.name, flag: teamData.flag || '', coach: teamData.coach || '', players: [] });
    saveData(data);
    return { success: true };
}

function addPlayerToTeam(teamName, player) {
    const data = getData();
    const team = data.teams.find(t => t.name === teamName);
    if (!team) return { success: false, msg: 'Equipa não encontrada.' };
    if (team.players.length >= 26) return { success: false, msg: 'Limite de 26 jogadores atingido.' };
    team.players.push({ name: player.name, number: player.number, position: player.position });
    saveData(data);
    return { success: true };
}

function removePlayerFromTeam(teamName, playerIndex) {
    const data = getData();
    const team = data.teams.find(t => t.name === teamName);
    if (!team) return false;
    team.players.splice(playerIndex, 1);
    saveData(data);
    return true;
}

function deleteMatch(matchId) {
    const data = getData();
    data.matches = data.matches.filter(m => m.id !== matchId);
    saveData(data);
    return true;
}

const DEFAULT_DATA = {
    matches: [
        // Dezasseis-avos (16 jogos - 32 equipas)
        { id: 1, date: '2026-06-25', time: '15:00', stadium: 'Estádio da Luz', homeTeam: 'Portugal', awayTeam: 'Irão', phase: 'Dezasseis-avos', status: 'Agendado', winner: null, evaluated: false, refPrincipal: null, refAssistente1: null, refVAR: null, refQuarto: null, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 2, date: '2026-06-25', time: '18:00', stadium: 'Estádio Alvalade', homeTeam: 'França', awayTeam: 'Ucrânia', phase: 'Dezasseis-avos', status: 'Agendado', winner: null, evaluated: false, refPrincipal: null, refAssistente1: null, refVAR: null, refQuarto: null, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 3, date: '2026-06-25', time: '21:00', stadium: 'Estádio do Dragão', homeTeam: 'Brasil', awayTeam: 'Chile', phase: 'Dezasseis-avos', status: 'Agendado', winner: null, evaluated: false, refPrincipal: null, refAssistente1: null, refVAR: null, refQuarto: null, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 4, date: '2026-06-26', time: '15:00', stadium: 'Camp Nou', homeTeam: 'Espanha', awayTeam: 'Turquia', phase: 'Dezasseis-avos', status: 'Agendado', winner: null, evaluated: false, refPrincipal: null, refAssistente1: null, refVAR: null, refQuarto: null, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 5, date: '2026-06-26', time: '18:00', stadium: 'Wembley', homeTeam: 'Argentina', awayTeam: 'Nigéria', phase: 'Dezasseis-avos', status: 'Agendado', winner: null, evaluated: false, refPrincipal: null, refAssistente1: null, refVAR: null, refQuarto: null, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 6, date: '2026-06-26', time: '21:00', stadium: 'Santiago Bernabéu', homeTeam: 'Inglaterra', awayTeam: 'Áustria', phase: 'Dezasseis-avos', status: 'Agendado', winner: null, evaluated: false, refPrincipal: null, refAssistente1: null, refVAR: null, refQuarto: null, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 7, date: '2026-06-27', time: '15:00', stadium: 'Azteca', homeTeam: 'Alemanha', awayTeam: 'Austrália', phase: 'Dezasseis-avos', status: 'Agendado', winner: null, evaluated: false, refPrincipal: null, refAssistente1: null, refVAR: null, refQuarto: null, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 8, date: '2026-06-27', time: '18:00', stadium: 'Rose Bowl', homeTeam: 'Holanda', awayTeam: 'Polónia', phase: 'Dezasseis-avos', status: 'Agendado', winner: null, evaluated: false, refPrincipal: null, refAssistente1: null, refVAR: null, refQuarto: null, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        
        { id: 9, date: '2026-06-27', time: '21:00', stadium: 'Estádio da Luz', homeTeam: 'Bélgica', awayTeam: 'Sérvia', phase: 'Dezasseis-avos', status: 'Agendado', winner: null, evaluated: false, refPrincipal: null, refAssistente1: null, refVAR: null, refQuarto: null, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 10, date: '2026-06-28', time: '15:00', stadium: 'Estádio Alvalade', homeTeam: 'Uruguai', awayTeam: 'Dinamarca', phase: 'Dezasseis-avos', status: 'Agendado', winner: null, evaluated: false, refPrincipal: null, refAssistente1: null, refVAR: null, refQuarto: null, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 11, date: '2026-06-28', time: '18:00', stadium: 'Estádio do Dragão', homeTeam: 'Croácia', awayTeam: 'Canadá', phase: 'Dezasseis-avos', status: 'Agendado', winner: null, evaluated: false, refPrincipal: null, refAssistente1: null, refVAR: null, refQuarto: null, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 12, date: '2026-06-28', time: '21:00', stadium: 'Camp Nou', homeTeam: 'Itália', awayTeam: 'Gana', phase: 'Dezasseis-avos', status: 'Agendado', winner: null, evaluated: false, refPrincipal: null, refAssistente1: null, refVAR: null, refQuarto: null, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 13, date: '2026-06-29', time: '15:00', stadium: 'Wembley', homeTeam: 'Marrocos', awayTeam: 'Coreia do Sul', phase: 'Dezasseis-avos', status: 'Agendado', winner: null, evaluated: false, refPrincipal: null, refAssistente1: null, refVAR: null, refQuarto: null, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 14, date: '2026-06-29', time: '18:00', stadium: 'Santiago Bernabéu', homeTeam: 'Colômbia', awayTeam: 'Japão', phase: 'Dezasseis-avos', status: 'Agendado', winner: null, evaluated: false, refPrincipal: null, refAssistente1: null, refVAR: null, refQuarto: null, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 15, date: '2026-06-29', time: '21:00', stadium: 'Azteca', homeTeam: 'Suíça', awayTeam: 'Senegal', phase: 'Dezasseis-avos', status: 'Agendado', winner: null, evaluated: false, refPrincipal: null, refAssistente1: null, refVAR: null, refQuarto: null, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 16, date: '2026-06-30', time: '18:00', stadium: 'Rose Bowl', homeTeam: 'EUA', awayTeam: 'Cuba', phase: 'Dezasseis-avos', status: 'Agendado', winner: null, evaluated: false, refPrincipal: null, refAssistente1: null, refVAR: null, refQuarto: null, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        
        // Próximas fases
        { id: 17, date: '2026-07-02', time: '18:00', stadium: 'Estádio da Luz', homeTeam: '', awayTeam: '', phase: 'Oitavos', status: 'Agendado', winner: null, evaluated: false, refPrincipal: null, refAssistente1: null, refVAR: null, refQuarto: null, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 18, date: '2026-07-02', time: '21:00', stadium: 'Estádio Alvalade', homeTeam: '', awayTeam: '', phase: 'Oitavos', status: 'Agendado', winner: null, evaluated: false, refPrincipal: null, refAssistente1: null, refVAR: null, refQuarto: null, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 19, date: '2026-07-03', time: '18:00', stadium: 'Estádio do Dragão', homeTeam: '', awayTeam: '', phase: 'Oitavos', status: 'Agendado', winner: null, evaluated: false, refPrincipal: null, refAssistente1: null, refVAR: null, refQuarto: null, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 20, date: '2026-07-03', time: '21:00', stadium: 'Camp Nou', homeTeam: '', awayTeam: '', phase: 'Oitavos', status: 'Agendado', winner: null, evaluated: false, refPrincipal: null, refAssistente1: null, refVAR: null, refQuarto: null, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 21, date: '2026-07-04', time: '18:00', stadium: 'Wembley', homeTeam: '', awayTeam: '', phase: 'Oitavos', status: 'Agendado', winner: null, evaluated: false, refPrincipal: null, refAssistente1: null, refVAR: null, refQuarto: null, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 22, date: '2026-07-04', time: '21:00', stadium: 'Santiago Bernabéu', homeTeam: '', awayTeam: '', phase: 'Oitavos', status: 'Agendado', winner: null, evaluated: false, refPrincipal: null, refAssistente1: null, refVAR: null, refQuarto: null, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 23, date: '2026-07-05', time: '18:00', stadium: 'Azteca', homeTeam: '', awayTeam: '', phase: 'Oitavos', status: 'Agendado', winner: null, evaluated: false, refPrincipal: null, refAssistente1: null, refVAR: null, refQuarto: null, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 24, date: '2026-07-05', time: '21:00', stadium: 'Rose Bowl', homeTeam: '', awayTeam: '', phase: 'Oitavos', status: 'Agendado', winner: null, evaluated: false, refPrincipal: null, refAssistente1: null, refVAR: null, refQuarto: null, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },

        { id: 25, date: '2026-07-08', time: '18:00', stadium: 'Estádio da Luz', homeTeam: '', awayTeam: '', phase: 'Quartos', status: 'Agendado', winner: null, evaluated: false, refPrincipal: null, refAssistente1: null, refVAR: null, refQuarto: null, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 26, date: '2026-07-08', time: '21:00', stadium: 'Camp Nou', homeTeam: '', awayTeam: '', phase: 'Quartos', status: 'Agendado', winner: null, evaluated: false, refPrincipal: null, refAssistente1: null, refVAR: null, refQuarto: null, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 27, date: '2026-07-09', time: '18:00', stadium: 'Wembley', homeTeam: '', awayTeam: '', phase: 'Quartos', status: 'Agendado', winner: null, evaluated: false, refPrincipal: null, refAssistente1: null, refVAR: null, refQuarto: null, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 28, date: '2026-07-09', time: '21:00', stadium: 'Santiago Bernabéu', homeTeam: '', awayTeam: '', phase: 'Quartos', status: 'Agendado', winner: null, evaluated: false, refPrincipal: null, refAssistente1: null, refVAR: null, refQuarto: null, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },

        { id: 29, date: '2026-07-14', time: '20:00', stadium: 'Estádio da Luz', homeTeam: '', awayTeam: '', phase: 'Meias-Finais', status: 'Agendado', winner: null, evaluated: false, refPrincipal: null, refAssistente1: null, refVAR: null, refQuarto: null, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 30, date: '2026-07-15', time: '20:00', stadium: 'Santiago Bernabéu', homeTeam: '', awayTeam: '', phase: 'Meias-Finais', status: 'Agendado', winner: null, evaluated: false, refPrincipal: null, refAssistente1: null, refVAR: null, refQuarto: null, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },

        { id: 31, date: '2026-07-19', time: '21:00', stadium: 'Santiago Bernabéu', homeTeam: '', awayTeam: '', phase: 'Final', status: 'Agendado', winner: null, evaluated: false, refPrincipal: null, refAssistente1: null, refVAR: null, refQuarto: null, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) }
    ],
    teams: [
        { name: 'Portugal', coach: 'Roberto Martínez', players: [] },
        { name: 'França', coach: 'Didier Deschamps', players: [] },
        { name: 'Brasil', coach: 'Dorival Júnior', players: [] },
        { name: 'Espanha', coach: 'Luis de la Fuente', players: [] },
        { name: 'Argentina', coach: 'Lionel Scaloni', players: [] },
        { name: 'Inglaterra', coach: 'Gareth Southgate', players: [] },
        { name: 'Alemanha', coach: 'Julian Nagelsmann', players: [] },
        { name: 'Holanda', coach: 'Ronald Koeman', players: [] },
        { name: 'Bélgica', coach: 'Domenico Tedesco', players: [] },
        { name: 'Uruguai', coach: 'Marcelo Bielsa', players: [] },
        { name: 'Croácia', coach: 'Zlatko Dalić', players: [] },
        { name: 'Itália', coach: 'Luciano Spalletti', players: [] },
        { name: 'Marrocos', coach: 'Walid Regragui', players: [] },
        { name: 'Colômbia', coach: 'Néstor Lorenzo', players: [] },
        { name: 'Suíça', coach: 'Murat Yakin', players: [] },
        { name: 'EUA', coach: 'Gregg Berhalter', players: [] },
        { name: 'Irão', coach: 'Amir Ghalenoei', players: [] },
        { name: 'Ucrânia', coach: 'Serhiy Rebrov', players: [] },
        { name: 'Chile', coach: 'Ricardo Gareca', players: [] },
        { name: 'Turquia', coach: 'Vincenzo Montella', players: [] },
        { name: 'Nigéria', coach: 'Finidi George', players: [] },
        { name: 'Áustria', coach: 'Ralf Rangnick', players: [] },
        { name: 'Austrália', coach: 'Graham Arnold', players: [] },
        { name: 'Polónia', coach: 'Michał Probierz', players: [] },
        { name: 'Sérvia', coach: 'Dragan Stojković', players: [] },
        { name: 'Dinamarca', coach: 'Kasper Hjulmand', players: [] },
        { name: 'Canadá', coach: 'Jesse Marsch', players: [] },
        { name: 'Gana', coach: 'Otto Addo', players: [] },
        { name: 'Coreia do Sul', coach: 'Hwang Sun-hong', players: [] },
        { name: 'Japão', coach: 'Hajime Moriyasu', players: [] },
        { name: 'Senegal', coach: 'Aliou Cissé', players: [] },
        { name: 'Cuba', coach: 'Yunielys Castillo', players: [] }
    ],
    referees: [
        { id: 1, email: 'ref1@fifa.com', name: 'Artur Soares Dias', nationality: 'Portugal', type: 'Principal', status: 'Ativo', matchesCount: 0, score: 0, evaluationsCount: 0 },
        { id: 2, email: 'ref2@fifa.com', name: 'Szymon Marciniak', nationality: 'Polónia', type: 'Principal', status: 'Ativo', matchesCount: 0, score: 0, evaluationsCount: 0 },
        { id: 3, email: 'ref3@fifa.com', name: 'Daniele Orsato', nationality: 'Itália', type: 'Principal', status: 'Ativo', matchesCount: 0, score: 0, evaluationsCount: 0 },
        { id: 4, email: 'ref4@fifa.com', name: 'Clément Turpin', nationality: 'França', type: 'Principal', status: 'Ativo', matchesCount: 0, score: 0, evaluationsCount: 0 },
        { id: 5, email: 'ref5@fifa.com', name: 'Facundo Tello', nationality: 'Argentina', type: 'Principal', status: 'Ativo', matchesCount: 0, score: 0, evaluationsCount: 0 },
        { id: 6, email: 'ref6@fifa.com', name: 'Wilton Sampaio', nationality: 'Brasil', type: 'VAR', status: 'Ativo', matchesCount: 0, score: 0, evaluationsCount: 0 },
        { id: 7, email: 'ref7@fifa.com', name: 'Massimiliano Irrati', nationality: 'Itália', type: 'VAR', status: 'Ativo', matchesCount: 0, score: 0, evaluationsCount: 0 },
        { id: 8, email: 'ref8@fifa.com', name: 'Tiago Martins', nationality: 'Portugal', type: 'VAR', status: 'Ativo', matchesCount: 0, score: 0, evaluationsCount: 0 },
        { id: 9, email: 'ref9@fifa.com', name: 'Pau Cebrián Devís', nationality: 'Espanha', type: 'Assistente', status: 'Ativo', matchesCount: 0, score: 0, evaluationsCount: 0 },
        { id: 10, email: 'ref10@fifa.com', name: 'Nicolas Danos', nationality: 'França', type: 'Assistente', status: 'Ativo', matchesCount: 0, score: 0, evaluationsCount: 0 }
    ],
    fraudLogs: [],
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
    logs: [],
    version: 10
};

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

function evaluateMatch(matchId, generalRating, refEvaluations) {
    const data = getData();
    const match = data.matches.find(m => m.id === matchId);
    if (!match) return false;

    match.evaluated = true;
    match.generalRating = generalRating;

    Object.entries(refEvaluations).forEach(([slot, rating]) => {
        const refName = match['ref' + slot];
        if (!refName) return;

        const ref = data.referees.find(r => r.name === refName);
        if (ref) {
            const oldCount = ref.evaluationsCount || 0;
            const oldScore = ref.score || 0;
            const newScore = ((oldScore * oldCount) + (rating * 20)) / (oldCount + 1);
            
            ref.score = Math.round(newScore);
            ref.evaluationsCount = oldCount + 1;
        }
    });

    saveData(data);
    return true;
}

// Inicialização do LocalStorage
function initDB() {
    const stored = localStorage.getItem('wc_data');
    if (!stored) {
        localStorage.setItem('wc_data', JSON.stringify(DEFAULT_DATA));
    } else {
        let currentData = JSON.parse(stored);
        let updated = false;

        // Migração: Garante que todos os jogos têm eventos, titulares e tickets
        currentData.matches.forEach(m => {
            if (!m.events) { m.events = []; updated = true; }
            if (!m.titulares) { m.titulares = {}; updated = true; }
            if (!m.tickets) { m.tickets = JSON.parse(JSON.stringify(DEFAULT_TICKETS)); updated = true; }
        });

        // Garante que a estrutura global de tickets existe
        if (!currentData.tickets) {
            currentData.tickets = DEFAULT_DATA.tickets;
            updated = true;
        }

        if (currentData.version !== DEFAULT_DATA.version) {
            currentData.version = DEFAULT_DATA.version;
            updated = true;
        }

        if (updated) {
            saveData(currentData);
            console.log('Base de dados migrada com sucesso.');
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
