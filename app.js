const DEFAULT_DATA = {
    matches: [
        { id: 1, date: '2026-06-10', time: '18:00', stadium: 'Estádio da Luz', homeTeam: 'Portugal', awayTeam: 'Espanha', phase: 'Grupos', status: 'Agendado', referees: [], events: [],
          tickets: [{category:'VIP', price:500, total:500, sold:480}, {category:'Cat 1', price:150, total:5000, sold:4200}, {category:'Geral', price:75, total:45000, sold:41000}], fraudAlerts: 1 },
        { id: 2, date: '2026-06-11', time: '20:00', stadium: 'Estádio Alvalade', homeTeam: 'Brasil', awayTeam: 'França', phase: 'Grupos', status: 'Agendado', referees: [], events: [],
          tickets: [{category:'VIP', price:600, total:400, sold:395}, {category:'Cat 1', price:200, total:4000, sold:3800}, {category:'Geral', price:100, total:35000, sold:32000}], fraudAlerts: 2 },
        { id: 10, date: '2026-07-19', time: '21:00', stadium: 'Santiago Bernabéu', homeTeam: 'Brasil', awayTeam: 'Alemanha', phase: 'Final', status: 'Agendado', referees: [], events: [],
          tickets: [{category:'VIP', price:2500, total:1000, sold:990}, {category:'Cat 1', price:800, total:8000, sold:7950}, {category:'Geral', price:350, total:60000, sold:59800}], fraudAlerts: 0 }
    ],
    fraudLogs: [
        { id: 101, timestamp: '2026-05-13 10:15:22', type: 'Bilhete Duplicado', severity: 'Crítico', description: 'Tentativa de entrada com bilhete ID #8842 já validado.', status: 'Pendente', match: 'Portugal vs Espanha' },
        { id: 102, timestamp: '2026-05-13 11:02:10', type: 'IP Duplicado', severity: 'Médio', description: 'IP 192.168.1.45 associado a 12 compras consecutivas de bilhetes Cat 1.', status: 'Pendente', match: 'Brasil vs França' },
        { id: 105, timestamp: '2026-05-13 13:10:00', type: 'Bilhete Duplicado', severity: 'Crítico', description: 'QR Code forjado detetado no sistema de pré-validação.', status: 'Pendente', match: 'Brasil vs França' }
    ],
    users: [
        { email: 'admin@fifa.com', role: 'Administrador', name: 'Super Admin' },
        { email: 'bil@fifa.com', role: 'Gestor de Bilheteira', name: 'Chefe Bilheteira' },
        { email: 'arb@fifa.com', role: 'Gestor de Arbitragem', name: 'Chefe Árbitro' }
    ]
};

function initDB() {
    localStorage.setItem('wc_data', JSON.stringify(DEFAULT_DATA));
}

function getData() { return JSON.parse(localStorage.getItem('wc_data')); }
function saveData(data) { localStorage.setItem('wc_data', JSON.stringify(data)); }
function getCurrentUser() { return JSON.parse(sessionStorage.getItem('wc_current_user')) || { role: 'Público' }; }

function injectRoleSwitcher() {
    const switcher = document.createElement('div');
    switcher.id = 'role-switcher';
    switcher.innerHTML = `
        <div style="background: #111827; color: white; padding: 15px; border-radius: 15px; box-shadow: 0 10px 30px rgba(0,0,0,0.3); border: 1px solid #374151;">
            <p style="font-size: 0.7rem; margin-bottom: 8px; color: #9CA3AF; text-transform: uppercase; font-weight: 800;">Simulador de Roles</p>
            <select id="role-select" style="padding: 8px; width: 100%; border-radius: 8px; border: none; background: #374151; color: white; margin-bottom: 10px; font-weight: 600;">
                <option value="Público">🌐 Público</option>
                <option value="Gestor de Bilheteira">🎟️ Gestor Bilheteira</option>
                <option value="Gestor de Arbitragem">⚖️ Gestor Árbitros</option>
                <option value="Administrador">🛡️ Administrador</option>
            </select>
            <button onclick="changeRole()" style="width: 100%; background: #00D26A; border: none; color: white; padding: 10px; border-radius: 8px; font-weight: 800; cursor: pointer;">Mudar Vista</button>
        </div>
    `;
    switcher.style.cssText = "position:fixed; bottom:20px; right:20px; z-index:9999;";
    document.body.appendChild(switcher);
}

function changeRole() {
    const newRole = document.getElementById('role-select').value;
    const data = getData();
    if (newRole === 'Público') { sessionStorage.removeItem('wc_current_user'); window.location.href = 'publico.html'; return; }
    const user = data.users.find(u => u.role === newRole);
    sessionStorage.setItem('wc_current_user', JSON.stringify(user));
    const routes = { 'Gestor de Bilheteira': 'gestor_bilheteira.html', 'Gestor de Arbitragem': 'gestor_arbitragem.html', 'Administrador': 'admin.html' };
    window.location.href = routes[newRole] || 'index.html';
}

document.addEventListener('DOMContentLoaded', () => { initDB(); injectRoleSwitcher(); });
