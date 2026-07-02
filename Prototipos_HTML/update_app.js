const fs = require('fs');
let content = fs.readFileSync('app.js', 'utf8');

const regex = /tickets:\s*\[.*?\]/g;
const newTicketsStr = "tickets: [{category: 'Premium', price: 200, sold: 100, total: 500}, {category: 'Intermediária', price: 150, sold: 500, total: 1000}, {category: 'Económica', price: 100, sold: 2000, total: 5000}, {category: 'Local', price: 50, sold: 3000, total: 10000}]";

content = content.replace(regex, newTicketsStr);

// Update global tickets structure too
content = content.replace(/categories:\s*\[[\s\S]*?\]/m, "categories: [{id: 'Premium', price: 200, sold: 1800, capacity: 9000, status: 'Aberto'}, {id: 'Intermediária', price: 150, sold: 9000, capacity: 18000, status: 'Aberto'}, {id: 'Económica', price: 100, sold: 36000, capacity: 90000, status: 'Aberto'}, {id: 'Local', price: 50, sold: 54000, capacity: 180000, status: 'Aberto'}]");

content = content.replace(/version !== 4/g, 'version !== 5');
content = content.replace(/version: 4/g, 'version: 5');
content = content.replace(/version = 4/g, 'version = 5');

fs.writeFileSync('app.js', content);
