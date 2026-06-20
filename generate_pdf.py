import os
import sys
import re
from fpdf import FPDF

class WC2026PDF(FPDF):
    def header(self):
        if self.page_no() > 1:
            self.set_font("Arial", "I", 8)
            self.set_text_color(100, 100, 100)
            self.set_x(self.l_margin)
            self.cell(self.epw, 10, "Sistema de Gestão do Campeonato do Mundo de Futebol 2026 | Fase 2", align="R")
            self.ln(12)
            # Add a thin line under header
            self.set_draw_color(200, 200, 200)
            self.line(self.l_margin, 22, self.w - self.r_margin, 22)
            self.ln(4)

    def footer(self):
        if self.page_no() > 1:
            self.set_y(-15)
            self.set_font("Arial", "I", 8)
            self.set_text_color(100, 100, 100)
            self.set_x(self.l_margin)
            self.cell(self.epw, 10, f"Página {self.page_no()}", align="C")

def create_pdf():
    pdf = WC2026PDF()
    pdf.set_margins(20, 20, 20)
    pdf.alias_nb_pages()
    pdf.set_auto_page_break(auto=True, margin=20)

    # Load system TTF fonts for Unicode support
    pdf.add_font("Arial", "", r"C:\Windows\Fonts\arial.ttf")
    pdf.add_font("Arial", "B", r"C:\Windows\Fonts\arialbd.ttf")
    pdf.add_font("Arial", "I", r"C:\Windows\Fonts\ariali.ttf")
    pdf.add_font("Arial", "BI", r"C:\Windows\Fonts\arialbi.ttf")
    
    pdf.add_font("CourierNew", "", r"C:\Windows\Fonts\cour.ttf")
    pdf.add_font("CourierNew", "B", r"C:\Windows\Fonts\courbd.ttf")

    # Load resources early to prevent missing variables
    with open("texto_casos_uso_iconix.md", "r", encoding="utf-8") as f:
        uc_content = f.read()
    sections = uc_content.split("---")

    with open("documentacao_fase2/relatorio_testes_unitarios.md", "r", encoding="utf-8") as f:
        tests_content = f.read()

    # ================= PAGE 1: COVER PAGE =================
    pdf.add_page()
    pdf.set_y(35)
    
    # Title
    pdf.set_font("Arial", "B", 24)
    pdf.set_text_color(24, 76, 120)  # Premium Blue
    pdf.cell(pdf.epw, 15, "Campeonato do Mundo FIFA 2026", align="C")
    pdf.ln(15)
    
    pdf.set_font("Arial", "B", 18)
    pdf.set_text_color(40, 40, 40)
    pdf.cell(pdf.epw, 12, "Sistema de Gestão do Torneio", align="C")
    pdf.ln(12)
    
    # Subtitle
    pdf.ln(5)
    pdf.set_font("Arial", "", 13)
    pdf.set_text_color(80, 80, 80)
    pdf.cell(pdf.epw, 10, "Relatório Técnico - Fase 2", align="C")
    pdf.ln(10)
    pdf.cell(pdf.epw, 8, "Casos de Uso ICONIX, Implementação & Testes Unitários", align="C")
    pdf.ln(8)
    
    # Decorative line
    pdf.ln(10)
    pdf.set_draw_color(24, 76, 120)
    pdf.set_line_width(1)
    pdf.line(50, pdf.get_y(), 160, pdf.get_y())
    pdf.ln(15)
    
    # Course / Institution Details
    pdf.set_font("Arial", "B", 12)
    pdf.set_text_color(50, 50, 50)
    pdf.cell(pdf.epw, 8, "UC: Engenharia de Software", align="C")
    pdf.ln(8)
    pdf.set_font("Arial", "", 11)
    pdf.cell(pdf.epw, 8, "Docente: Professor Pedro Gago", align="C")
    pdf.ln(8)
    
    # GitHub Repository Link
    pdf.ln(5)
    pdf.set_font("Arial", "B", 10)
    pdf.set_text_color(24, 76, 120)
    pdf.cell(pdf.epw, 8, "Repositório GitHub:", align="C")
    pdf.ln(6)
    pdf.set_font("Arial", "U", 9.5)
    pdf.set_text_color(30, 100, 200)
    github_url = "https://github.com/experimentos373-creator/ES.git"
    pdf.cell(pdf.epw, 6, github_url, align="C", link=github_url)
    pdf.ln(10)
    
    # Team info
    pdf.set_fill_color(245, 247, 250)
    pdf.rect(25, pdf.get_y(), pdf.w - 50, 48, "F")
    
    pdf.set_y(pdf.get_y() + 4)
    pdf.set_font("Arial", "B", 11)
    pdf.set_text_color(24, 76, 120)
    pdf.cell(pdf.epw, 6, "Grupo de Trabalho:", align="C")
    pdf.ln(6)
    
    pdf.set_font("Arial", "", 10)
    pdf.set_text_color(50, 50, 50)
    pdf.cell(pdf.epw, 6, "Paulo Gomes (2024134892) (Administrador + Bracket + Integração)", align="C")
    pdf.ln(6)
    pdf.cell(pdf.epw, 6, "Leonardo Mendes (Gestão de Equipa + Arbitragem)", align="C")
    pdf.ln(6)
    pdf.cell(pdf.epw, 6, "Arthur (Gestão de Logística + Bilheteira)", align="C")
    pdf.ln(6)
    
    # Date/Location
    pdf.set_y(260)
    pdf.set_font("Arial", "I", 9)
    pdf.set_text_color(100, 100, 100)
    pdf.cell(pdf.epw, 6, "Junho de 2026", align="C")
    pdf.ln(6)
    pdf.cell(pdf.epw, 6, "Coimbra, Portugal", align="C")

    # ================= PAGE 2: ENUNCIADO E DIRECTRIZES =================
    pdf.add_page()
    pdf.set_y(30)
    
    pdf.set_font("Arial", "B", 16)
    pdf.set_text_color(24, 76, 120)
    pdf.cell(pdf.epw, 10, "1. Enquadramento e Requisitos Pedagógicos")
    pdf.ln(12)
    
    pdf.set_font("Arial", "", 10)
    pdf.set_text_color(40, 40, 40)
    
    enquadramento_text = (
        "Este documento técnico sintetiza os entregáveis da Fase 2 para a unidade curricular de Engenharia de Software. "
        "O projeto consiste no desenvolvimento do Sistema de Gestão do Campeonato do Mundo de Futebol 2026, com foco nas "
        "seguintes diretrizes estipuladas:\n\n"
        "1. Especificação de Casos de Uso sob a metodologia ICONIX (25%):\n"
        "   - Passos rigorosamente numerados no fluxo principal e fluxos alternativos.\n"
        "   - Identificação explícita dos objetos de fronteira (boundary) entre aspas, por exemplo, \"Menu de Navegação\".\n"
        "   - Entidades do domínio em maiúsculas (ex: Equipa, Jogo, Arbitro, Estadio, Bilhete).\n"
        "   - Narrativa em linguagem natural detalhando a interação ator <-> sistema.\n"
        "   - Ausência de formalismos excessivos e redundantes como tabelas de pré/pós-condições.\n\n"
        "2. Implementação Java (30%):\n"
        "   - Código limpo, robusto e totalmente coerente com os fluxos dos casos de uso descritos.\n"
        "   - Utilização de padrões de desenho como Singleton para a persistência simulada em base de dados e gestão de estado.\n\n"
        "3. Testes Unitários (5%):\n"
        "   - Escrita de 3 testes unitários focados estritamente na lógica de negócio por cada membro do grupo.\n"
        "   - Cobertura de regras críticas como elegibilidade de árbitros (nacionalidade e descanso), limites anti-bot na bilheteira, "
        "capacidades de alojamento de equipas e critérios de desempate desportivo.\n"
        "   - Ausência de dependências de interfaces gráficas (GUI) ou base de dados física, validando apenas a lógica pura."
    )
    
    pdf.multi_cell(pdf.epw, 6, enquadramento_text)
    pdf.ln(8)
    
    # Repositório GitHub
    pdf.set_font("Arial", "B", 11)
    pdf.set_text_color(24, 76, 120)
    pdf.cell(pdf.epw, 8, "Ligação para o Repositório do Projeto:")
    pdf.ln(8)
    pdf.set_font("Arial", "", 10)
    pdf.set_text_color(50, 50, 50)
    pdf.write(5, "O código-fonte completo, histórico de commits e os testes unitários encontram-se disponíveis em: ")
    pdf.set_font("Arial", "U", 10)
    pdf.set_text_color(30, 100, 200)
    pdf.write(5, github_url, github_url)
    pdf.ln(10)

    # ================= PAGE 3: DIVISÃO DE TRABALHO =================
    pdf.add_page()
    pdf.set_y(30)
    pdf.set_font("Arial", "B", 16)
    pdf.set_text_color(24, 76, 120)
    pdf.cell(pdf.epw, 10, "1.1 Divisão e Distribuição de Trabalho")
    pdf.ln(12)
    
    pdf.set_font("Arial", "", 10)
    pdf.set_text_color(40, 40, 40)
    divisao_intro = (
        "A fim de garantir equidade na distribuição do esforço de desenvolvimento do projeto e cobrir os requisitos "
        "individuais de avaliação técnica estabelecidos, o grupo distribuiu as responsabilidades de modulação, "
        "casos de uso, implementação de lógica e correspondentes testes unitários de acordo com o seguinte plano:"
    )
    pdf.multi_cell(pdf.epw, 6, divisao_intro)
    pdf.ln(8)
    
    # Division of Work Table using new FPDF2 table API
    work_data = [
        ("Paulo Gomes\n(2024134892)", 
         "Administrador (Geral), Bracket/Tabelas de Eliminatórias, Integração Geral e Gestor de Bilheteira (Performance).\nImplementação Java da persistência Singleton (base dados simulada), classes de classificação de grupos e bracket.", 
         "GrupoClassificacaoTest\nCalendarioJogoTest\nAvancoBracketTest\nLotacaoEstadioTest\nScoreFIFATest\nSigiloArbitrosTest"),
         
        ("Leonardo Mendes", 
         "Gestor de Arbitragem (Escala, Avaliação de Árbitros) e Público/Adepto (Calendário de Jogos e Resultados).\nImplementação Java da lógica de regras éticas e de descanso de árbitros.", 
         "JogadorStateTest\nNeutralidadeArbitroTest\nIntervaloArbitroTest"),
         
        ("Arthur", 
         "Gestor de Logística (Alojamentos de Seleções/Hotéis, Transportes) e Público/Adepto (Compra de Bilhetes - Regras anti-bot).\nImplementação Java de controlo de capacidades de hotelaria e anti-bot.", 
         "AlojamentoCapacidadeTest (Capacidade)\nAlojamentoCapacidadeTest (Exclusividade)\nAntiBotBilheteiraTest")
    ]

    # Style header row colors
    pdf.set_font("Arial", "B", 9)
    pdf.set_text_color(255, 255, 255)
    pdf.set_fill_color(24, 76, 120)

    with pdf.table(col_widths=(30, 90, 50), text_align="LEFT") as table:
        # Header Row
        row = table.row()
        row.cell("Membro do Grupo")
        row.cell("Módulos & Responsabilidades Técnicas")
        row.cell("Testes Unitários")
        
        # Style content rows colors (RESET fill to white, text to grey)
        pdf.set_font("Arial", "", 8.5)
        pdf.set_text_color(50, 50, 50)
        pdf.set_fill_color(255, 255, 255)
        for name, responsibilities, tests_list in work_data:
            row = table.row()
            row.cell(name)
            row.cell(responsibilities)
            row.cell(tests_list)
            
    pdf.ln(10)

    # ================= PAGE 4: TEXTO CASOS DE USO =================
    pdf.add_page()
    pdf.set_y(30)
    pdf.set_font("Arial", "B", 16)
    pdf.set_text_color(24, 76, 120)
    pdf.cell(pdf.epw, 10, "2. Texto dos Casos de Uso (Metodologia ICONIX)")
    pdf.ln(12)
    
    # Skip the header section in markdown (index 0) and start printing
    for section in sections[1:]:
        section = section.strip()
        if not section:
            continue
        
        if pdf.get_y() > 220:
            pdf.add_page()
            pdf.set_y(30)
            
        lines = section.split("\n")
        in_alternatives = False
        
        for line in lines:
            line = line.strip()
            if not line:
                continue
            
            if line.startswith("## "):
                pdf.ln(5)
                pdf.set_font("Arial", "B", 13)
                pdf.set_text_color(24, 76, 120)
                pdf.set_x(pdf.l_margin)
                pdf.cell(pdf.epw, 8, line.replace("## ", ""))
                pdf.ln(10)
                
            elif line.startswith("### "):
                pdf.ln(4)
                pdf.set_x(pdf.l_margin)
                pdf.set_fill_color(240, 244, 248)
                pdf.set_font("Arial", "B", 11)
                pdf.set_text_color(30, 30, 30)
                pdf.cell(pdf.epw, 8, f"  {line.replace('### ', '')}", fill=True)
                pdf.ln(10)
                in_alternatives = False
                
            elif "Caminhos Alternativos" in line:
                pdf.ln(2)
                pdf.set_x(pdf.l_margin)
                pdf.set_font("Arial", "B", 9.5)
                pdf.set_text_color(120, 50, 50)
                pdf.cell(pdf.epw, 6, "Caminhos Alternativos:")
                pdf.ln(6)
                in_alternatives = True
                
            else:
                pdf.set_x(pdf.l_margin)
                pdf.set_font("Arial", "", 9.5)
                if in_alternatives:
                    pdf.set_text_color(90, 40, 40)
                else:
                    pdf.set_text_color(50, 50, 50)
                
                pdf.multi_cell(pdf.epw, 5, line)
                pdf.ln(1)

    # ================= PAGE: TESTES UNITARIOS =================
    pdf.add_page()
    pdf.set_y(30)
    pdf.set_font("Arial", "B", 16)
    pdf.set_text_color(24, 76, 120)
    pdf.cell(pdf.epw, 10, "3. Relatório de Testes Unitários Realizados")
    pdf.ln(12)
    
    # Let's write a summary intro
    pdf.set_font("Arial", "", 10)
    pdf.set_text_color(40, 40, 40)
    intro_tests = (
        "Com o objetivo de assegurar que as regras críticas de negócio descritas nos Casos de Uso são rigorosamente "
        "validadas no código-fonte, foi desenvolvida uma suíte de testes unitários abrangente. Cada elemento do "
        "grupo de trabalho assumiu a responsabilidade direta pelo desenvolvimento de 3 testes focados na lógica dos seus respetivos módulos."
    )
    pdf.multi_cell(pdf.epw, 6, intro_tests)
    pdf.ln(6)
    
    # Let's draw the distribution table
    pdf.set_font("Arial", "B", 11)
    pdf.set_text_color(24, 76, 120)
    pdf.cell(pdf.epw, 8, "Distribuição das Responsabilidades de Testes")
    pdf.ln(10)
    
    table_data = [
        ("Leonardo", "JogadorStateTest\nNeutralidadeArbitroTest\nIntervaloArbitroTest", "Equipa + Arbitragem", "Elegibilidade e repouso de árbitros, transição de lesões."),
        ("Arthur", "AlojamentoCapacidadeTest (x2)\nAntiBotBilheteiraTest", "Logística + Bilheteira", "Exclusividade de hotel, limites na compra de bilhetes (1-4)."),
        ("Paulo", "GrupoClassificacaoTest\nCalendarioJogoTest\nAvancoBracketTest\nLotacaoEstadioTest\nScoreFIFATest\nSigiloArbitrosTest", "Admin, Calendário, Bracket, Bilheteira", "Desempates FIFA, progressão de bracket, sigilo de escalas, lotações.")
    ]
    
    # Style header row colors
    pdf.set_font("Arial", "B", 9)
    pdf.set_text_color(255, 255, 255)
    pdf.set_fill_color(24, 76, 120)
    
    with pdf.table(col_widths=(25, 50, 45, 50), text_align="LEFT") as table:
        # Header Row
        row = table.row()
        row.cell("Elemento")
        row.cell("Testes Atribuídos")
        row.cell("Módulos")
        row.cell("Foco de Validação")
        
        # Style content rows colors (RESET fill to white, text to grey)
        pdf.set_font("Arial", "", 8.5)
        pdf.set_text_color(50, 50, 50)
        pdf.set_fill_color(255, 255, 255)
        for elem, tests, modules, validation in table_data:
            row = table.row()
            row.cell(elem)
            row.cell(tests)
            row.cell(modules)
            row.cell(validation)
            
    pdf.ln(10)
    
    # Detail tests by element
    pdf.add_page()
    pdf.set_y(30)
    pdf.set_font("Arial", "B", 14)
    pdf.set_text_color(24, 76, 120)
    pdf.cell(pdf.epw, 10, "3.1 Detalhe Técnico dos Casos de Teste (AAA)")
    pdf.ln(12)
    
    # Parse the detail section of tests
    lines = tests_content.split("\n")
    in_detail_section = False
    
    for line in lines:
        line = line.strip()
        if "## 🧪 Detalhe Técnico" in line:
            in_detail_section = True
            continue
        if "## 💻 Resultados de Execução" in line:
            in_detail_section = False
            
        if in_detail_section:
            if not line:
                continue
            if line.startswith("### "):
                pdf.ln(4)
                pdf.set_font("Arial", "B", 12)
                pdf.set_text_color(24, 76, 120)
                pdf.set_x(pdf.l_margin)
                pdf.cell(pdf.epw, 8, line.replace("### ", ""))
                pdf.ln(10)
            elif line.startswith("#### "):
                # Remove emoji like 👤 and link markup if present
                clean_line = line.replace("#### ", "")
                clean_line = clean_line.replace("👤 ", "")
                clean_line = re.sub(r'\[(.*?)\]\(.*?\)', r'\1', clean_line)
                pdf.ln(2)
                pdf.set_font("Arial", "B", 10.5)
                pdf.set_text_color(30, 30, 30)
                pdf.set_x(pdf.l_margin)
                pdf.cell(pdf.epw, 6, clean_line)
                pdf.ln(8)
            elif line.startswith("* "):
                pdf.set_x(pdf.l_margin)
                pdf.set_font("Arial", "", 9.5)
                pdf.set_text_color(60, 60, 60)
                pdf.multi_cell(pdf.epw, 5, line)
                pdf.ln(1)
            elif line.startswith("- "):
                pdf.set_x(pdf.l_margin)
                pdf.set_font("Arial", "", 9.5)
                pdf.set_text_color(70, 70, 70)
                pdf.multi_cell(pdf.epw, 5, "    " + line)
                pdf.ln(1)
            else:
                pdf.set_x(pdf.l_margin)
                pdf.set_font("Arial", "", 9.5)
                pdf.set_text_color(50, 50, 50)
                pdf.multi_cell(pdf.epw, 5, line)
                pdf.ln(1)
                
    # ================= PAGE: MAVEN OUTPUT =================
    pdf.add_page()
    pdf.set_y(30)
    pdf.set_font("Arial", "B", 14)
    pdf.set_text_color(24, 76, 120)
    pdf.cell(pdf.epw, 10, "3.2 Resultados de Execução do Test Runner (Maven)")
    pdf.ln(12)
    
    pdf.set_font("Arial", "", 10)
    pdf.set_text_color(50, 50, 50)
    pdf.multi_cell(pdf.epw, 5.5, "Todos os 22 testes unitários (incluindo os 12 de lógica atribuídos explicitamente aos elementos do grupo) foram executados e passaram com sucesso, sem erros ou falhas. A execução foi realizada através do Maven:")
    pdf.ln(6)
    
    # Extract maven block
    maven_block = []
    in_maven = False
    for line in lines:
        if "```text" in line or "```" in line and in_maven:
            in_maven = not in_maven
            continue
        if "[INFO] Scanning for projects..." in line:
            in_maven = True
        if in_maven:
            maven_block.append(line)
            
    # Print maven output in CourierNew inside a box
    pdf.set_fill_color(245, 245, 245)
    pdf.set_font("CourierNew", "", 8)
    pdf.set_text_color(30, 30, 30)
    
    x, y = pdf.get_x(), pdf.get_y()
    box_height = len(maven_block) * 4.2 + 6
    
    if y + box_height > 270:
        pdf.add_page()
        pdf.set_y(30)
        x, y = pdf.get_x(), pdf.get_y()
        
    pdf.rect(x, y, pdf.epw, box_height, "F")
    pdf.set_xy(x + 3, y + 3)
    
    for m_line in maven_block:
        pdf.cell(pdf.epw - 6, 4.2, m_line)
        pdf.ln(4.2)
        
    pdf.ln(10)
    
    # ================= PAGE: COERENCIA DA IMPLEMENTACAO =================
    pdf.add_page()
    pdf.set_y(30)
    pdf.set_font("Arial", "B", 16)
    pdf.set_text_color(24, 76, 120)
    pdf.cell(pdf.epw, 10, "4. Coerência da Implementação com os Casos de Uso")
    pdf.ln(12)
    
    pdf.set_font("Arial", "", 10)
    pdf.set_text_color(40, 40, 40)
    
    coerencia_text = (
        "A arquitetura de software implementada em Java reflete com total exatidão a modelagem conceitual dos Casos de Uso. "
        "As principais entidades do domínio do campeonato (Equipa, Jogo, Arbitro, Estadio, Bilhete e Alojamento) foram mapeadas "
        "como classes puras no pacote 'domain' do projeto.\n\n"
        "Pontos Críticos de Alinhamento e Coerência:\n\n"
        "1. Gestão e Registo de Escalas (CU06):\n"
        "   - O fluxo do caso de uso exige que o sistema registe o 'EscalaoArbitral' na base de dados associado a um Jogo.\n"
        "   - No código-fonte, a classe correspondente 'EscalaoArbitral' valida e guarda a associação de 5 árbitros por partida, "
        "garantindo que não existem conflitos éticos. Os testes unitários (como NeutralidadeArbitroTest e IntervaloArbitroTest) "
        "validam exatamente esta restrição de integridade no ato da atribuição.\n\n"
        "2. Lotações de Estádios e Vendas de Ingressos (CU14, CU16, CU23):\n"
        "   - O caso de uso CU23 (Comprar Bilhetes) descreve o controlo de lugares no Setor do Estadio e a regra anti-bot (compra limitada a 4 bilhetes).\n"
        "   - No código, o método 'comprarBilhetes()' valida as restrições e lança exceções adequadas que impedem a transação, "
        "conforme verificado pelo teste unitário 'AntiBotBilheteiraTest'.\n\n"
        "3. Alojamento e Exclusividade Logística (CU19):\n"
        "   - O caso de uso exige que o Hotel tenha capacidade suficiente para albergar o plantel e que apenas uma seleção possa ser alocada por hotel.\n"
        "   - O código valida e impede check-ins simultâneos ou capacidade excedida, coerente com os testes 'AlojamentoCapacidadeTest'.\n\n"
        "4. Bracket de Eliminatórias (CU03):\n"
        "   - A finalização do jogo (CU03) desencadeia a progressão automática no bracket de eliminatórias.\n"
        "   - A classe 'Bracket' e o modelo de dados atualizam dinamicamente a próxima fase do torneio ao finalizar um Jogo, "
        "o que foi demonstrado pelo teste 'AvancoBracketTest'."
    )
    pdf.multi_cell(pdf.epw, 6, coerencia_text)
    
    # Save the PDF with fallback if locked
    output_path = "documentacao_fase2/Relatorio_Fase2.pdf"
    fallback_path = "documentacao_fase2/Relatorio_Fase2_Pedro_Gago.pdf"
    try:
        pdf.output(output_path)
        print(f"PDF successfully generated: {output_path}")
    except PermissionError:
        print(f"Permission denied on {output_path}. Falling back to {fallback_path}")
        pdf.output(fallback_path)
        print(f"PDF successfully generated fallback: {fallback_path}")

if __name__ == "__main__":
    create_pdf()
