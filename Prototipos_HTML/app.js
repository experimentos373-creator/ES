const STADIUMS_LIST = ['Estádio da Luz','Estádio Alvalade','Estádio do Dragão','Camp Nou','Wembley','Santiago Bernabéu','Azteca','Rose Bowl'];
const PHASES_LIST = ['Grupos','Dezasseis-avos','Oitavos','Quartos','Meias-Finais','Final'];
const DEFAULT_TICKETS = [
    {category:'Premium',price:200,sold:0,total:500},
    {category:'Intermediária',price:150,sold:0,total:1000},
    {category:'Económica',price:100,sold:0,total:5000},
    {category:'Local',price:50,sold:0,total:10000}
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

function finalizeMatch(matchId, resultData) {
    const data = getData();
    const match = data.matches.find(m => m.id === matchId);
    if (!match) return false;
    
    match.status = 'Finalizado';
    match.winner = resultData.winner;
    match.goalsHome = resultData.goalsHome;
    match.goalsAway = resultData.goalsAway;
    match.penaltiesHome = resultData.penaltiesHome || null;
    match.penaltiesAway = resultData.penaltiesAway || null;
    if (resultData.events) match.events = resultData.events;
    
    // Ensure stats structure exists
    const defaultStats = {
        posHome: 50, posAway: 50, shotsHome: 10, shotsAway: 10, targetHome: 4, targetAway: 4,
        cornersHome: 4, cornersAway: 4, offsidesHome: 1, offsidesAway: 1, foulsHome: 10, foulsAway: 10,
        yellowsHome: 1, yellowsAway: 1, redsHome: 0, redsAway: 0, savesHome: 3, savesAway: 3,
        passesHome: 400, passesAway: 400, passaccHome: 80, passaccAway: 80
    };
    match.stats = Object.assign({}, defaultStats, resultData.stats);

    // Generate individual player ratings and save to snapshot & player history
    const homeTeamObj = data.teams.find(t => t.name === match.homeTeam);
    const awayTeamObj = data.teams.find(t => t.name === match.awayTeam);
    
    match.playersSnapshot = { home: [], away: [] };
    
    [ { teamObj: homeTeamObj, list: match.playersSnapshot.home, isHome: true, goalsTeam: match.goalsHome, goalsOpp: match.goalsAway, saves: match.stats.savesHome },
      { teamObj: awayTeamObj, list: match.playersSnapshot.away, isHome: false, goalsTeam: match.goalsAway, goalsOpp: match.goalsHome, saves: match.stats.savesAway }
    ].forEach(({ teamObj, list, isHome, goalsTeam, goalsOpp, saves }) => {
        if (!teamObj || !teamObj.players) return;
        teamObj.players.forEach(p => {
            const played = p.isStarter || p.starter || match.events.some(e => e.player === p.name);
            if (!played) return;
            
            // Dynamic rating: 6.0 baseline + random match fluctuation (±1.5)
            let rating = 6.0 + (Math.random() * 3.0 - 1.5);
            let minutes = 90;
            
            const playerGoals = match.events.filter(e => e.player === p.name && e.type === 'Golo' && e.team === teamObj.name).length;
            const playerYellows = match.events.filter(e => e.player === p.name && e.type === 'Cartão Amarelo').length;
            const playerReds = match.events.filter(e => e.player === p.name && e.type === 'Cartão Vermelho').length;
            
            rating += playerGoals * 1.5;
            rating -= playerYellows * 0.5;
            rating -= playerReds * 1.5;
            
            if (p.position === 'Guarda-Redes' || p.pos === 'Guarda-Redes') {
                rating += saves * 0.3;
            }
            
            rating = Math.max(3.0, Math.min(10.0, rating));
            rating = parseFloat(rating.toFixed(1));
            
            const snapPlayer = {
                id: p.id,
                name: p.name,
                number: p.number,
                position: p.position || p.pos || 'Avançado',
                rating: rating,
                minutes: minutes,
                goals: playerGoals,
                yellow: playerYellows,
                red: playerReds
            };
            list.push(snapPlayer);
            
            // Save to general team database for player history tab
            p.performances = p.performances || [];
            p.performances = p.performances.filter(perf => perf.matchId !== match.id);
            p.performances.push({
                matchId: match.id,
                rating: rating,
                minutes: minutes,
                goals: playerGoals,
                yellow: playerYellows,
                red: playerReds
            });
            
            p.goals = (p.goals || 0) + playerGoals;
            p.yellow = (p.yellow || 0) + playerYellows;
            p.red = (p.red || 0) + playerReds;
        });
    });

    const phaseOrder = ['Grupos', 'Dezasseis-avos','Oitavos','Quartos','Meias-Finais','Final'];
    const currentIdx = phaseOrder.indexOf(match.phase);
    
    // Avanço automático se for eliminatória
    if (currentIdx >= 1 && currentIdx < phaseOrder.length - 1) {
        const nextPhase = phaseOrder[currentIdx + 1];
        const samePhaseMatches = data.matches.filter(m => m.phase === match.phase).sort((a,b) => a.id - b.id);
        const matchIndex = samePhaseMatches.findIndex(m => m.id === matchId);
        const pairIndex = Math.floor(matchIndex / 2);
        const nextMatches = data.matches.filter(m => m.phase === nextPhase).sort((a,b) => a.id - b.id);
        if (nextMatches[pairIndex]) {
            const slot = (matchIndex % 2 === 0) ? 'homeTeam' : 'awayTeam';
            nextMatches[pairIndex][slot] = resultData.winner;
        }
    }
    
    saveData(data);

    // Se for fase de Grupos, verifica se podemos avançar para os Oitavos
    if (match.phase === 'Grupos') {
        checkAndAdvanceGroupsToOitavos();
    }

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
        // Dezasseis-avos (16 jogos)
        { 
            id: 1, date: '2026-06-25', time: '15:00', stadium: 'Estádio da Luz', homeTeam: 'Portugal', awayTeam: 'Cuba', phase: 'Dezasseis-avos', status: 'Finalizado', winner: 'Portugal', evaluated: false, 
            refPrincipal: 'Artur Soares Dias', refAssistente1: 'Pau Cebrián Devís', refVAR: 'Wilton Sampaio', refQuarto: 'Clément Turpin', 
            goalsHome: 3, goalsAway: 0,
            events: [
                { type: 'Golo', minute: 23, player: 'Cristiano Ronaldo', team: 'Portugal' },
                { type: 'Golo', minute: 45, player: 'Bruno Fernandes', team: 'Portugal' },
                { type: 'Golo', minute: 78, player: 'Cristiano Ronaldo', team: 'Portugal' },
                { type: 'Cartão Amarelo', minute: 34, player: 'Pepe Silva', team: 'Portugal' }
            ], 
            tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)),
            stats: {
                posHome: 65, posAway: 35, shotsHome: 18, shotsAway: 4, targetHome: 8, targetAway: 1,
                cornersHome: 7, cornersAway: 2, offsidesHome: 2, offsidesAway: 1, foulsHome: 8, foulsAway: 12,
                yellowsHome: 1, yellowsAway: 2, redsHome: 0, redsAway: 0, savesHome: 1, savesAway: 5,
                passesHome: 580, passesAway: 290, passaccHome: 88, passaccAway: 70
            },
            playersSnapshot: {
                home: [
                    { id: 1, name: 'Diogo Costa', number: 1, position: 'Guarda-Redes', rating: 7.2, minutes: 90, goals: 0, yellow: 0, red: 0 },
                    { id: 3, name: 'Pepe Silva', number: 3, position: 'Guarda-Redes', rating: 6.8, minutes: 90, goals: 0, yellow: 1, red: 0 },
                    { id: 4, name: 'Rúben Dias', number: 4, position: 'Defesa', rating: 7.5, minutes: 90, goals: 0, yellow: 0, red: 0 },
                    { id: 5, name: 'Nuno Mendes', number: 5, position: 'Defesa', rating: 7.3, minutes: 90, goals: 0, yellow: 0, red: 0 },
                    { id: 6, name: 'João Cancelo', number: 6, position: 'Defesa', rating: 7.8, minutes: 90, goals: 0, yellow: 0, red: 0 },
                    { id: 7, name: 'Bruno Fernandes', number: 7, position: 'Defesa', rating: 8.9, minutes: 90, goals: 1, yellow: 0, red: 0 },
                    { id: 8, name: 'Bernardo Silva', number: 8, position: 'Defesa', rating: 8.0, minutes: 90, goals: 0, yellow: 0, red: 0 },
                    { id: 9, name: 'Rafael Leão', number: 9, position: 'Defesa', rating: 7.6, minutes: 90, goals: 0, yellow: 0, red: 0 },
                    { id: 10, name: 'Cristiano Ronaldo', number: 10, position: 'Defesa', rating: 9.6, minutes: 90, goals: 2, yellow: 0, red: 0 },
                    { id: 11, name: 'Gonçalo Ramos', number: 11, position: 'Defesa', rating: 6.9, minutes: 90, goals: 0, yellow: 0, red: 0 }
                ],
                away: [
                    { id: 101, name: 'Y. Castillo Player 1', number: 1, position: 'Guarda-Redes', rating: 5.5, minutes: 90, goals: 0, yellow: 0, red: 0 },
                    { id: 102, name: 'Y. Castillo Player 2', number: 2, position: 'Defesa', rating: 5.0, minutes: 90, goals: 0, yellow: 1, red: 0 },
                    { id: 103, name: 'Y. Castillo Player 3', number: 3, position: 'Defesa', rating: 5.2, minutes: 90, goals: 0, yellow: 1, red: 0 },
                    { id: 104, name: 'Y. Castillo Player 4', number: 4, position: 'Médio', rating: 6.0, minutes: 90, goals: 0, yellow: 0, red: 0 },
                    { id: 105, name: 'Y. Castillo Player 5', number: 5, position: 'Avançado', rating: 5.8, minutes: 90, goals: 0, yellow: 0, red: 0 }
                ]
            }
        },
        { 
            id: 2, date: '2026-06-25', time: '18:00', stadium: 'Estádio Alvalade', homeTeam: 'França', awayTeam: 'Japão', phase: 'Dezasseis-avos', status: 'Finalizado', winner: 'França', evaluated: false, 
            refPrincipal: 'Szymon Marciniak', refAssistente1: 'Nicolas Danos', refVAR: 'Massimiliano Irrati', refQuarto: 'Facundo Tello', 
            goalsHome: 2, goalsAway: 1,
            events: [
                { type: 'Golo', minute: 15, player: 'Kylian Mbappé', team: 'França' },
                { type: 'Golo', minute: 40, player: 'Japão Player 9', team: 'Japão' },
                { type: 'Golo', minute: 82, player: 'Antoine Griezmann', team: 'França' }
            ], 
            tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)),
            stats: {
                posHome: 58, posAway: 42, shotsHome: 14, shotsAway: 8, targetHome: 6, targetAway: 3,
                cornersHome: 5, cornersAway: 3, offsidesHome: 3, offsidesAway: 2, foulsHome: 11, foulsAway: 14,
                yellowsHome: 1, yellowsAway: 1, redsHome: 0, redsAway: 0, savesHome: 2, savesAway: 4,
                passesHome: 490, passesAway: 360, passaccHome: 84, passaccAway: 78
            },
            playersSnapshot: {
                home: [
                    { id: 1, name: 'Mike Maignan', number: 1, position: 'Guarda-Redes', rating: 7.0, minutes: 90, goals: 0, yellow: 0, red: 0 },
                    { id: 4, name: 'Dayot Upamecano', number: 4, position: 'Defesa', rating: 7.2, minutes: 90, goals: 0, yellow: 0, red: 0 },
                    { id: 5, name: 'Ibrahima Konaté', number: 5, position: 'Defesa', rating: 7.1, minutes: 90, goals: 0, yellow: 0, red: 0 },
                    { id: 6, name: 'Theo Hernandez', number: 6, position: 'Defesa', rating: 7.5, minutes: 90, goals: 0, yellow: 0, red: 0 },
                    { id: 9, name: 'Antoine Griezmann', number: 9, position: 'Defesa', rating: 8.5, minutes: 90, goals: 1, yellow: 0, red: 0 },
                    { id: 10, name: 'Kylian Mbappé', number: 10, position: 'Defesa', rating: 8.8, minutes: 90, goals: 1, yellow: 0, red: 0 },
                    { id: 11, name: 'Olivier Giroud', number: 11, position: 'Defesa', rating: 6.8, minutes: 90, goals: 0, yellow: 0, red: 0 }
                ],
                away: [
                    { id: 201, name: 'Japão Player 1', number: 1, position: 'Guarda-Redes', rating: 6.8, minutes: 90, goals: 0, yellow: 0, red: 0 },
                    { id: 209, name: 'Japão Player 9', number: 9, position: 'Avançado', rating: 8.0, minutes: 90, goals: 1, yellow: 0, red: 0 }
                ]
            }
        },
        { 
            id: 3, date: '2026-06-25', time: '21:00', stadium: 'Estádio do Dragão', homeTeam: 'Espanha', awayTeam: 'Senegal', phase: 'Dezasseis-avos', status: 'Finalizado', winner: 'Espanha', evaluated: false, 
            refPrincipal: 'Daniele Orsato', refAssistente1: 'Pau Cebrián Devís', refVAR: 'Tiago Martins', refQuarto: 'Clément Turpin', 
            goalsHome: 1, goalsAway: 0,
            events: [
                { type: 'Golo', minute: 67, player: 'Espanha Player 10', team: 'Espanha' }
            ], 
            tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)),
            stats: {
                posHome: 62, posAway: 38, shotsHome: 12, shotsAway: 5, targetHome: 5, targetAway: 2,
                cornersHome: 6, cornersAway: 1, offsidesHome: 1, offsidesAway: 3, foulsHome: 9, foulsAway: 15,
                yellowsHome: 1, yellowsAway: 2, redsHome: 0, redsAway: 0, savesHome: 2, savesAway: 4,
                passesHome: 550, passesAway: 310, passaccHome: 87, passaccAway: 72
            },
            playersSnapshot: {
                home: [
                    { id: 301, name: 'Espanha Player 1', number: 1, position: 'Guarda-Redes', rating: 7.4, minutes: 90, goals: 0, yellow: 0, red: 0 },
                    { id: 310, name: 'Espanha Player 10', number: 10, position: 'Avançado', rating: 8.2, minutes: 90, goals: 1, yellow: 0, red: 0 }
                ],
                away: [
                    { id: 401, name: 'Senegal Player 1', number: 1, position: 'Guarda-Redes', rating: 7.0, minutes: 90, goals: 0, yellow: 0, red: 0 }
                ]
            }
        },
        { 
            id: 4, date: '2026-06-26', time: '15:00', stadium: 'Camp Nou', homeTeam: 'Brasil', awayTeam: 'Marrocos', phase: 'Dezasseis-avos', status: 'Finalizado', winner: 'Brasil', evaluated: false, 
            refPrincipal: 'Facundo Tello', refAssistente1: 'Nicolas Danos', refVAR: 'Wilton Sampaio', refQuarto: 'Artur Soares Dias', 
            goalsHome: 2, goalsAway: 0,
            events: [
                { type: 'Golo', minute: 30, player: 'Vinícius Júnior', team: 'Brasil' },
                { type: 'Golo', minute: 75, player: 'Rodrygo Goes', team: 'Brasil' }
            ], 
            tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)),
            stats: {
                posHome: 55, posAway: 45, shotsHome: 15, shotsAway: 7, targetHome: 7, targetAway: 2,
                cornersHome: 4, cornersAway: 3, offsidesHome: 2, offsidesAway: 1, foulsHome: 10, foulsAway: 13,
                yellowsHome: 2, yellowsAway: 1, redsHome: 0, redsAway: 0, savesHome: 2, savesAway: 5,
                passesHome: 470, passesAway: 380, passaccHome: 83, passaccAway: 76
            },
            playersSnapshot: {
                home: [
                    { id: 1, name: 'Alisson Becker', number: 1, position: 'Guarda-Redes', rating: 7.5, minutes: 90, goals: 0, yellow: 0, red: 0 },
                    { id: 4, name: 'Danilo Luiz', number: 4, position: 'Defesa', rating: 7.2, minutes: 90, goals: 0, yellow: 0, red: 0 },
                    { id: 5, name: 'Marquinhos Corrêa', number: 5, position: 'Defesa', rating: 7.4, minutes: 90, goals: 0, yellow: 0, red: 0 },
                    { id: 8, name: 'Casemiro Casemiro', number: 8, position: 'Defesa', rating: 7.8, minutes: 90, goals: 0, yellow: 1, red: 0 },
                    { id: 11, name: 'Vinícius Júnior', number: 11, position: 'Defesa', rating: 9.0, minutes: 90, goals: 1, yellow: 0, red: 0 },
                    { id: 12, name: 'Rodrygo Goes', number: 12, position: 'Médio', rating: 8.5, minutes: 90, goals: 1, yellow: 0, red: 0 }
                ],
                away: [
                    { id: 501, name: 'Marrocos Player 1', number: 1, position: 'Guarda-Redes', rating: 6.8, minutes: 90, goals: 0, yellow: 0, red: 0 }
                ]
            }
        },
        { id: 5, date: '2026-06-26', time: '18:00', stadium: 'Wembley', homeTeam: 'Argentina', awayTeam: 'EUA', phase: 'Dezasseis-avos', status: 'Agendado', winner: null, evaluated: false, refPrincipal: null, refAssistente1: null, refVAR: null, refQuarto: null, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 6, date: '2026-06-26', time: '21:00', stadium: 'Santiago Bernabéu', homeTeam: 'Inglaterra', awayTeam: 'Holanda', phase: 'Dezasseis-avos', status: 'Agendado', winner: null, evaluated: false, refPrincipal: null, refAssistente1: null, refVAR: null, refQuarto: null, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 7, date: '2026-06-27', time: '15:00', stadium: 'Azteca', homeTeam: 'Alemanha', awayTeam: 'Itália', phase: 'Dezasseis-avos', status: 'Agendado', winner: null, evaluated: false, refPrincipal: null, refAssistente1: null, refVAR: null, refQuarto: null, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 8, date: '2026-06-27', time: '18:00', stadium: 'Rose Bowl', homeTeam: 'Colômbia', awayTeam: 'Suíça', phase: 'Dezasseis-avos', status: 'Agendado', winner: null, evaluated: false, refPrincipal: null, refAssistente1: null, refVAR: null, refQuarto: null, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 9, date: '2026-06-27', time: '21:00', stadium: 'Estádio da Luz', homeTeam: 'Bélgica', awayTeam: 'Coreia do Sul', phase: 'Dezasseis-avos', status: 'Agendado', winner: null, evaluated: false, refPrincipal: null, refAssistente1: null, refVAR: null, refQuarto: null, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 10, date: '2026-06-28', time: '15:00', stadium: 'Estádio Alvalade', homeTeam: 'Uruguai', awayTeam: 'Gana', phase: 'Dezasseis-avos', status: 'Agendado', winner: null, evaluated: false, refPrincipal: null, refAssistente1: null, refVAR: null, refQuarto: null, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 11, date: '2026-06-28', time: '18:00', stadium: 'Estádio do Dragão', homeTeam: 'Croácia', awayTeam: 'Canadá', phase: 'Dezasseis-avos', status: 'Agendado', winner: null, evaluated: false, refPrincipal: null, refAssistente1: null, refVAR: null, refQuarto: null, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 12, date: '2026-06-28', time: '21:00', stadium: 'Camp Nou', homeTeam: 'Dinamarca', awayTeam: 'Sérvia', phase: 'Dezasseis-avos', status: 'Agendado', winner: null, evaluated: false, refPrincipal: null, refAssistente1: null, refVAR: null, refQuarto: null, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 13, date: '2026-06-29', time: '15:00', stadium: 'Wembley', homeTeam: 'Polónia', awayTeam: 'Austrália', phase: 'Dezasseis-avos', status: 'Agendado', winner: null, evaluated: false, refPrincipal: null, refAssistente1: null, refVAR: null, refQuarto: null, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 14, date: '2026-06-29', time: '18:00', stadium: 'Santiago Bernabéu', homeTeam: 'Áustria', awayTeam: 'Nigéria', phase: 'Dezasseis-avos', status: 'Agendado', winner: null, evaluated: false, refPrincipal: null, refAssistente1: null, refVAR: null, refQuarto: null, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 15, date: '2026-06-29', time: '21:00', stadium: 'Azteca', homeTeam: 'Turquia', awayTeam: 'Chile', phase: 'Dezasseis-avos', status: 'Agendado', winner: null, evaluated: false, refPrincipal: null, refAssistente1: null, refVAR: null, refQuarto: null, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 16, date: '2026-06-30', time: '18:00', stadium: 'Rose Bowl', homeTeam: 'Ucrânia', awayTeam: 'Irão', phase: 'Dezasseis-avos', status: 'Agendado', winner: null, evaluated: false, refPrincipal: null, refAssistente1: null, refVAR: null, refQuarto: null, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        
        // Oitavos
        { id: 17, date: '2026-07-02', time: '18:00', stadium: 'Estádio da Luz', homeTeam: 'Portugal', awayTeam: 'França', phase: 'Oitavos', status: 'Agendado', winner: null, evaluated: false, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 18, date: '2026-07-02', time: '21:00', stadium: 'Estádio Alvalade', homeTeam: 'Espanha', awayTeam: 'Brasil', phase: 'Oitavos', status: 'Agendado', winner: null, evaluated: false, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 19, date: '2026-07-03', time: '18:00', stadium: 'Estádio do Dragão', homeTeam: '', awayTeam: '', phase: 'Oitavos', status: 'Agendado', winner: null, evaluated: false, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 20, date: '2026-07-03', time: '21:00', stadium: 'Camp Nou', homeTeam: '', awayTeam: '', phase: 'Oitavos', status: 'Agendado', winner: null, evaluated: false, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 21, date: '2026-07-04', time: '18:00', stadium: 'Wembley', homeTeam: '', awayTeam: '', phase: 'Oitavos', status: 'Agendado', winner: null, evaluated: false, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 22, date: '2026-07-04', time: '21:00', stadium: 'Santiago Bernabéu', homeTeam: '', awayTeam: '', phase: 'Oitavos', status: 'Agendado', winner: null, evaluated: false, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 23, date: '2026-07-05', time: '18:00', stadium: 'Azteca', homeTeam: '', awayTeam: '', phase: 'Oitavos', status: 'Agendado', winner: null, evaluated: false, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 24, date: '2026-07-05', time: '21:00', stadium: 'Rose Bowl', homeTeam: '', awayTeam: '', phase: 'Oitavos', status: 'Agendado', winner: null, evaluated: false, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },

        // Quartos
        { id: 25, date: '2026-07-08', time: '18:00', stadium: 'Estádio da Luz', homeTeam: '', awayTeam: '', phase: 'Quartos', status: 'Agendado', winner: null, evaluated: false, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 26, date: '2026-07-08', time: '21:00', stadium: 'Camp Nou', homeTeam: '', awayTeam: '', phase: 'Quartos', status: 'Agendado', winner: null, evaluated: false, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 27, date: '2026-07-09', time: '18:00', stadium: 'Wembley', homeTeam: '', awayTeam: '', phase: 'Quartos', status: 'Agendado', winner: null, evaluated: false, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 28, date: '2026-07-09', time: '21:00', stadium: 'Santiago Bernabéu', homeTeam: '', awayTeam: '', phase: 'Quartos', status: 'Agendado', winner: null, evaluated: false, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },

        // Meias-Finais
        { id: 29, date: '2026-07-14', time: '20:00', stadium: 'Estádio da Luz', homeTeam: '', awayTeam: '', phase: 'Meias-Finais', status: 'Agendado', winner: null, evaluated: false, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },
        { id: 30, date: '2026-07-15', time: '20:00', stadium: 'Santiago Bernabéu', homeTeam: '', awayTeam: '', phase: 'Meias-Finais', status: 'Agendado', winner: null, evaluated: false, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) },

        // Final
        { id: 31, date: '2026-07-19', time: '21:00', stadium: 'Santiago Bernabéu', homeTeam: '', awayTeam: '', phase: 'Final', status: 'Agendado', winner: null, evaluated: false, events: [], tickets: JSON.parse(JSON.stringify(DEFAULT_TICKETS)) }
    ],
    teams: [
        { name: 'Portugal', coach: 'Roberto Martínez', players: [{"id":1,"number":1,"name":"Diogo Costa","position":"Guarda-Redes","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":true,"minutes":0,"energy":100,"injuryHistory":[]},{"id":2,"number":2,"name":"Rui Patrício","position":"Guarda-Redes","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":true,"minutes":0,"energy":100,"injuryHistory":[]},{"id":3,"number":3,"name":"Pepe Silva","position":"Guarda-Redes","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":true,"minutes":0,"energy":100,"injuryHistory":[]},{"id":4,"number":4,"name":"Rúben Dias","position":"Defesa","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":true,"minutes":0,"energy":100,"injuryHistory":[]},{"id":5,"number":5,"name":"Nuno Mendes","position":"Defesa","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":true,"minutes":0,"energy":100,"injuryHistory":[]},{"id":6,"number":6,"name":"João Cancelo","position":"Defesa","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":true,"minutes":0,"energy":100,"injuryHistory":[]},{"id":7,"number":7,"name":"Bruno Fernandes","position":"Defesa","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":true,"minutes":0,"energy":100,"injuryHistory":[]},{"id":8,"number":8,"name":"Bernardo Silva","position":"Defesa","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":true,"minutes":0,"energy":100,"injuryHistory":[]},{"id":9,"number":9,"name":"Rafael Leão","position":"Defesa","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":true,"minutes":0,"energy":100,"injuryHistory":[]},{"id":10,"number":10,"name":"Cristiano Ronaldo","position":"Defesa","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":true,"minutes":0,"energy":100,"injuryHistory":[]},{"id":11,"number":11,"name":"Gonçalo Ramos","position":"Defesa","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":true,"minutes":0,"energy":100,"injuryHistory":[]},{"id":12,"number":12,"name":"Vitinha Ferreira","position":"Médio","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":13,"number":13,"name":"Francisco Conceição","position":"Médio","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":14,"number":14,"name":"Otávio Monteiro","position":"Médio","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":15,"number":15,"name":"Danilo Pereira","position":"Médio","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":16,"number":16,"name":"António Silva","position":"Médio","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":17,"number":17,"name":"Diogo Dalot","position":"Médio","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":18,"number":18,"name":"José Sá","position":"Médio","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":19,"number":19,"name":"Nélson Semedo","position":"Avançado","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":20,"number":20,"name":"Pedro Neto","position":"Avançado","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":21,"number":21,"name":"Matheus Nunes","position":"Avançado","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":22,"number":22,"name":"Toti Gomes","position":"Avançado","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":23,"number":23,"name":"João Félix","position":"Avançado","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":24,"number":24,"name":"Francisco Trincão","position":"Avançado","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":25,"number":25,"name":"João Neves","position":"Avançado","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":26,"number":26,"name":"Beto Beto","position":"Avançado","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]}] },
        { name: 'Cuba', coach: 'Yunielys Castillo', players: [] },
        { name: 'França', coach: 'Didier Deschamps', players: [{"id":1,"number":1,"name":"Mike Maignan","position":"Guarda-Redes","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":true,"minutes":0,"energy":100,"injuryHistory":[]},{"id":2,"number":2,"name":"Alphonse Areola","position":"Guarda-Redes","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":true,"minutes":0,"energy":100,"injuryHistory":[]},{"id":3,"number":3,"name":"Benjamin Pavard","position":"Guarda-Redes","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":true,"minutes":0,"energy":100,"injuryHistory":[]},{"id":4,"number":4,"name":"Dayot Upamecano","position":"Defesa","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":true,"minutes":0,"energy":100,"injuryHistory":[]},{"id":5,"number":5,"name":"Ibrahima Konaté","position":"Defesa","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":true,"minutes":0,"energy":100,"injuryHistory":[]},{"id":6,"number":6,"name":"Theo Hernandez","position":"Defesa","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":true,"minutes":0,"energy":100,"injuryHistory":[]},{"id":7,"number":7,"name":"Aurélien Tchouaméni","position":"Defesa","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":true,"minutes":0,"energy":100,"injuryHistory":[]},{"id":8,"number":8,"name":"Adrien Rabiot","position":"Defesa","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":true,"minutes":0,"energy":100,"injuryHistory":[]},{"id":9,"number":9,"name":"Antoine Griezmann","position":"Defesa","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":true,"minutes":0,"energy":100,"injuryHistory":[]},{"id":10,"number":10,"name":"Kylian Mbappé","position":"Defesa","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":true,"minutes":0,"energy":100,"injuryHistory":[]},{"id":11,"number":11,"name":"Olivier Giroud","position":"Defesa","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":true,"minutes":0,"energy":100,"injuryHistory":[]},{"id":12,"number":12,"name":"Ousmane Dembélé","position":"Médio","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":13,"number":13,"name":"Marcus Thuram","position":"Médio","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":14,"number":14,"name":"Eduardo Camavinga","position":"Médio","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":15,"number":15,"name":"Randal Kolo Muani","position":"Médio","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":16,"number":16,"name":"Jules Koundé","position":"Médio","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":17,"number":17,"name":"William Saliba","position":"Médio","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":18,"number":18,"name":"Ferland Mendy","position":"Médio","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":19,"number":19,"name":"Warren Zaïre-Emery","position":"Avançado","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":20,"number":20,"name":"Youssouf Fofana","position":"Avançado","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":21,"number":21,"name":"Moussa Diaby","position":"Avançado","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":22,"number":22,"name":"Axel Disasi","position":"Avançado","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":23,"number":23,"name":"Brice Samba","position":"Avançado","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":24,"number":24,"name":"Jonathan Clauss","position":"Avançado","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":25,"number":25,"name":"Kingsley Coman","position":"Avançado","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":26,"number":26,"name":"Bradley Barcola","position":"Avançado","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]}] },
        { name: 'Japão', coach: 'Hajime Moriyasu', players: [] },
        { name: 'Espanha', coach: 'Luis de la Fuente', players: [] },
        { name: 'Senegal', coach: 'Aliou Cissé', players: [] },
        { name: 'Brasil', coach: 'Dorival Júnior', players: [{"id":1,"number":1,"name":"Alisson Becker","position":"Guarda-Redes","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":true,"minutes":0,"energy":100,"injuryHistory":[]},{"id":2,"number":2,"name":"Ederson Moraes","position":"Guarda-Redes","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":true,"minutes":0,"energy":100,"injuryHistory":[]},{"id":3,"number":3,"name":"Bento Krepski","position":"Guarda-Redes","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":true,"minutes":0,"energy":100,"injuryHistory":[]},{"id":4,"number":4,"name":"Danilo Luiz","position":"Defesa","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":true,"minutes":0,"energy":100,"injuryHistory":[]},{"id":5,"number":5,"name":"Marquinhos Corrêa","position":"Defesa","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":true,"minutes":0,"energy":100,"injuryHistory":[]},{"id":6,"number":6,"name":"Gabriel Magalhães","position":"Defesa","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":true,"minutes":0,"energy":100,"injuryHistory":[]},{"id":7,"number":7,"name":"Wendell Borges","position":"Defesa","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":true,"minutes":0,"energy":100,"injuryHistory":[]},{"id":8,"number":8,"name":"Casemiro Casemiro","position":"Defesa","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":true,"minutes":0,"energy":100,"injuryHistory":[]},{"id":9,"number":9,"name":"Bruno Guimarães","position":"Defesa","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":true,"minutes":0,"energy":100,"injuryHistory":[]},{"id":10,"number":10,"name":"Lucas Paquetá","position":"Defesa","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":true,"minutes":0,"energy":100,"injuryHistory":[]},{"id":11,"number":11,"name":"Vinícius Júnior","position":"Defesa","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":true,"minutes":0,"energy":100,"injuryHistory":[]},{"id":12,"number":12,"name":"Rodrygo Goes","position":"Médio","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":13,"number":13,"name":"Raphinha Dias","position":"Médio","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":14,"number":14,"name":"Endrick Moreira","position":"Médio","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":15,"number":15,"name":"Douglas Luiz","position":"Médio","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":16,"number":16,"name":"Lucas Beraldo","position":"Médio","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":17,"number":17,"name":"Yan Couto","position":"Médio","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":18,"number":18,"name":"Bremer Bremer","position":"Médio","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":19,"number":19,"name":"Beraldo Beraldo","position":"Avançado","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":20,"number":20,"name":"Ayrton Lucas","position":"Avançado","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":21,"number":21,"name":"João Gomes","position":"Avançado","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":22,"number":22,"name":"Andreas Pereira","position":"Avançado","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":23,"number":23,"name":"Ederson Ederson","position":"Avançado","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":24,"number":24,"name":"Savinho Savinho","position":"Avançado","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":25,"number":25,"name":"Evanilson Evanilson","position":"Avançado","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":26,"number":26,"name":"Gabriel Martinelli","position":"Avançado","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]}] },
        { name: 'Marrocos', coach: 'Walid Regragui', players: [] },
        { name: 'Argentina', coach: 'Lionel Scaloni', players: [{"id":1,"number":1,"name":"Emiliano Martínez","position":"Guarda-Redes","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":true,"minutes":0,"energy":100,"injuryHistory":[]},{"id":2,"number":2,"name":"Franco Armani","position":"Guarda-Redes","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":true,"minutes":0,"energy":100,"injuryHistory":[]},{"id":3,"number":3,"name":"Gerónimo Rulli","position":"Guarda-Redes","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":true,"minutes":0,"energy":100,"injuryHistory":[]},{"id":4,"number":4,"name":"Nahuel Molina","position":"Defesa","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":true,"minutes":0,"energy":100,"injuryHistory":[]},{"id":5,"number":5,"name":"Cristian Romero","position":"Defesa","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":true,"minutes":0,"energy":100,"injuryHistory":[]},{"id":6,"number":6,"name":"Nicolás Otamendi","position":"Defesa","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":true,"minutes":0,"energy":100,"injuryHistory":[]},{"id":7,"number":7,"name":"Marcos Acuña","position":"Defesa","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":true,"minutes":0,"energy":100,"injuryHistory":[]},{"id":8,"number":8,"name":"Rodrigo De Paul","position":"Defesa","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":true,"minutes":0,"energy":100,"injuryHistory":[]},{"id":9,"number":9,"name":"Enzo Fernández","position":"Defesa","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":true,"minutes":0,"energy":100,"injuryHistory":[]},{"id":10,"number":10,"name":"Alexis Mac Allister","position":"Defesa","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":true,"minutes":0,"energy":100,"injuryHistory":[]},{"id":11,"number":11,"name":"Lionel Messi","position":"Defesa","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":true,"minutes":0,"energy":100,"injuryHistory":[]},{"id":12,"number":12,"name":"Julián Álvarez","position":"Médio","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":13,"number":13,"name":"Ángel Di María","position":"Médio","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":14,"number":14,"name":"Lautaro Martínez","position":"Médio","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":15,"number":15,"name":"Lisandro Martínez","position":"Médio","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":16,"number":16,"name":"Gonzalo Montiel","position":"Médio","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":17,"number":17,"name":"Germán Pezzella","position":"Médio","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":18,"number":18,"name":"Nicolás Tagliafico","position":"Médio","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":19,"number":19,"name":"Valentín Barco","position":"Avançado","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":20,"number":20,"name":"Exequiel Palacios","position":"Avançado","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":21,"number":21,"name":"Guido Rodríguez","position":"Avançado","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":22,"number":22,"name":"Giovani Lo Celso","position":"Avançado","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":23,"number":23,"name":"Leandro Paredes","position":"Avançado","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":24,"number":24,"name":"Alejandro Garnacho","position":"Avançado","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":25,"number":25,"name":"Paulo Dybala","position":"Avançado","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]},{"id":26,"number":26,"name":"Nicolás González","position":"Avançado","status":"Apto","goals":0,"assists":0,"injuries":0,"yellow":0,"red":0,"isStarter":false,"minutes":0,"energy":100,"injuryHistory":[]}] },
        { name: 'EUA', coach: 'Gregg Berhalter', players: [] },
        { name: 'Inglaterra', coach: 'Gareth Southgate', players: [] },
        { name: 'Holanda', coach: 'Ronald Koeman', players: [] },
        { name: 'Alemanha', coach: 'Julian Nagelsmann', players: [] },
        { name: 'Itália', coach: 'Luciano Spalletti', players: [] },
        { name: 'Colômbia', coach: 'Néstor Lorenzo', players: [] },
        { name: 'Suíça', coach: 'Murat Yakin', players: [] },
        { name: 'Bélgica', coach: 'Domenico Tedesco', players: [] },
        { name: 'Coreia do Sul', coach: 'Hwang Sun-hong', players: [] },
        { name: 'Uruguai', coach: 'Marcelo Bielsa', players: [] },
        { name: 'Gana', coach: 'Otto Addo', players: [] },
        { name: 'Croácia', coach: 'Zlatko Dalić', players: [] },
        { name: 'Canadá', coach: 'Jesse Marsch', players: [] },
        { name: 'Dinamarca', coach: 'Kasper Hjulmand', players: [] },
        { name: 'Sérvia', coach: 'Dragan Stojković', players: [] },
        { name: 'Polónia', coach: 'Michał Probierz', players: [] },
        { name: 'Austrália', coach: 'Graham Arnold', players: [] },
        { name: 'Áustria', coach: 'Ralf Rangnick', players: [] },
        { name: 'Nigéria', coach: 'Finidi George', players: [] },
        { name: 'Turquia', coach: 'Vincenzo Montella', players: [] },
        { name: 'Chile', coach: 'Ricardo Gareca', players: [] },
        { name: 'Ucrânia', coach: 'Serhiy Rebrov', players: [] },
        { name: 'Irão', coach: 'Amir Ghalenoei', players: [] }
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
    tickets: { sold: 0, available: 0, categories: [] },
    hotels: [
        { id: 1, name: 'Hotel Mundial', location: 'Lisboa', capacity: 100, team: 'Portugal', status: 'Ocupado' }
    ],
    users: [
        { email: 'admin@fifa.com', role: 'Administrador', name: 'Super Admin' },
        { email: 'arb@fifa.com', role: 'Gestor de Arbitragem', name: 'Chefe Árbitro' },
        { email: 'team@fpf.pt', role: 'Gestor de Equipa', name: 'Manager Portugal', team: 'Portugal' },
        { email: 'log@fifa.com', role: 'Gestor de Logística', name: 'Chefe Logística' },
    ],
    groups: [
        { name: 'Grupo A', teams: ['Portugal', 'Cuba', 'França', 'Japão'] },
        { name: 'Grupo B', teams: ['Espanha', 'Senegal', 'Brasil', 'Marrocos'] },
        { name: 'Grupo C', teams: ['Argentina', 'EUA', 'Inglaterra', 'Holanda'] },
        { name: 'Grupo D', teams: ['Alemanha', 'Itália', 'Colômbia', 'Suíça'] },
        { name: 'Grupo E', teams: ['Bélgica', 'Coreia do Sul', 'Uruguai', 'Gana'] },
        { name: 'Grupo F', teams: ['Croácia', 'Canadá', 'Dinamarca', 'Sérvia'] },
        { name: 'Grupo G', teams: ['Polónia', 'Austrália', 'Áustria', 'Nigéria'] },
        { name: 'Grupo H', teams: ['Turquia', 'Chile', 'Ucrânia', 'Irão'] }
    ],
    logs: [],
    version: 15
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

// Funções para Grupos
function calculateGroupStandings(groupName) {
    const data = getData();
    const group = data.groups.find(g => g.name === groupName);
    if (!group) return [];

    let standings = group.teams.map(teamName => ({
        name: teamName,
        played: 0, won: 0, drawn: 0, lost: 0,
        goalsFor: 0, goalsAgainst: 0,
        points: 0
    }));

    const groupMatches = data.matches.filter(m => m.phase === 'Grupos' && m.status === 'Finalizado' && (group.teams.includes(m.homeTeam) || group.teams.includes(m.awayTeam)));

    groupMatches.forEach(m => {
        let homeS = standings.find(s => s.name === m.homeTeam);
        let awayS = standings.find(s => s.name === m.awayTeam);
        if(!homeS || !awayS) return;

        // Se o jogo tiver golos explícitos, usamos esses. Caso contrário, contamos os eventos.
        let homeG = (m.goalsHome !== undefined && m.goalsHome !== null) ? m.goalsHome : 0;
        let awayG = (m.goalsAway !== undefined && m.goalsAway !== null) ? m.goalsAway : 0;

        // Se não houver golos explícitos, contamos os eventos que têm a propriedade 'team'
        if (homeG === 0 && awayG === 0 && m.events.length > 0) {
            homeG = m.events.filter(e => (e.type === 'Golo' && e.team === m.homeTeam) || (e.type === 'Auto-Golo' && e.team === m.awayTeam)).length;
            awayG = m.events.filter(e => (e.type === 'Golo' && e.team === m.awayTeam) || (e.type === 'Auto-Golo' && e.team === m.homeTeam)).length;
        }

        if (m.winner === m.homeTeam) {
            homeS.won++; awayS.lost++; homeS.points += 3;
        } else if (m.winner === m.awayTeam) {
            awayS.won++; homeS.lost++; awayS.points += 3;
        } else {
            homeS.drawn++; awayS.drawn++; homeS.points += 1; awayS.points += 1;
        }

        homeS.played++; awayS.played++;
        homeS.goalsFor += homeG; homeS.goalsAgainst += awayG;
        awayS.goalsFor += awayG; awayS.goalsAgainst += homeG;
    });

    standings.sort((a, b) => {
        if (b.points !== a.points) return b.points - a.points;
        const gdA = a.goalsFor - a.goalsAgainst;
        const gdB = b.goalsFor - b.goalsAgainst;
        if (gdB !== gdA) return gdB - gdA;
        return b.goalsFor - a.goalsFor;
    });

    return standings;
}

function checkAndAdvanceGroupsToOitavos() {
    const data = getData();
    const oitavosMatches = data.matches.filter(m => m.phase === 'Oitavos').sort((a,b) => a.id - b.id);
    if (oitavosMatches.length < 8) return; // Precisa de 8 slots

    let allGroupsFinished = true;
    const finalStandings = {};

    for (let group of data.groups) {
        const std = calculateGroupStandings(group.name);
        const isFinished = std.every(s => s.played >= 3);
        if (!isFinished) {
            allGroupsFinished = false;
            break;
        }
        finalStandings[group.name.replace('Grupo ', '')] = std;
    }

    if (allGroupsFinished) {
        // Mapeamento padrão FIFA para 32 equipas
        // 17: 1A x 2B | 18: 1C x 2D | 19: 1E x 2F | 20: 1G x 2H
        // 21: 1B x 2A | 22: 1D x 2C | 23: 1F x 2E | 24: 1H x 2G
        const matchups = [
            { id: oitavosMatches[0].id, home: 'A', away: 'B' },
            { id: oitavosMatches[1].id, home: 'C', away: 'D' },
            { id: oitavosMatches[2].id, home: 'E', away: 'F' },
            { id: oitavosMatches[3].id, home: 'G', away: 'H' },
            { id: oitavosMatches[4].id, home: 'B', away: 'A' },
            { id: oitavosMatches[5].id, home: 'D', away: 'C' },
            { id: oitavosMatches[6].id, home: 'F', away: 'E' },
            { id: oitavosMatches[7].id, home: 'H', away: 'G' }
        ];

        let changed = false;
        matchups.forEach(mMap => {
            const match = data.matches.find(m => m.id === mMap.id);
            if (match) {
                const homeTeamName = finalStandings[mMap.home][0].name; // 1º lugar
                const awayTeamName = finalStandings[mMap.away][1].name; // 2º lugar
                if (match.homeTeam !== homeTeamName || match.awayTeam !== awayTeamName) {
                    match.homeTeam = homeTeamName;
                    match.awayTeam = awayTeamName;
                    changed = true;
                }
            }
        });

        if (changed) {
            saveData(data);
            console.log('Grupos finalizados! Equipas apuradas para os Oitavos-de-Final automaticamente.');
        }
    }
}

// Inicialização do LocalStorage
function initDB() {
    const stored = localStorage.getItem('wc_data');
    if (!stored) {
        localStorage.setItem('wc_data', JSON.stringify(DEFAULT_DATA));
    } else {
        const data = JSON.parse(stored);
        if (data.version !== 14) {
            localStorage.setItem('wc_data', JSON.stringify(DEFAULT_DATA));
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
