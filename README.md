# 📅 Gerador de Cronograma de Aulas em Java

Projeto para ler arquivo aulas.txt e montar um cronograma semanal de aulas

---

## ⚙️ Pré-requisitos

- Java JDK 8 ou superior instalado
- Terminal (CMD, Bash, PowerShell, etc.)

Você pode verificar se o Java está instalado com:

```bash
java -version
javac -version
```

---

## 📁 Estrutura do Projeto

Coloque os seguintes arquivos no mesmo diretório:

```
/meu-projeto/
├── Cronograma.java
├── Aula.java
├── Intervalo.java
└── aulas.txt          ← Arquivo de entrada com as aulas
```

---

## 📝 Exemplo de conteúdo do `aulas.txt`

```text
Matemática - Prof. Silva 50
Física - Prof. Lima 45
História - Prof. Almeida 60
Filosofia - Prof. Cunha 30
Informática - Prof. Teixeira 50
Química - Prof. Andrade 60
```

Cada linha deve seguir o formato:
```
<NOME_DA_AULA> - Prof. <SOBRENOME> <DURAÇÃO_EM_MINUTOS>
```

---

## 🧪 Como compilar

Abra o terminal na pasta onde estão os arquivos e execute:

```bash
javac *.java
```

Se tudo correr bem, isso gerará os arquivos `.class` correspondentes.

---

## ▶️ Como executar

Após compilar, execute o programa com:

```bash
java Cronograma
```

---

## ✅ O que o programa faz

- Lê o arquivo `aulas.txt` linha por linha;
- Valida o formato da aula (nome, professor, duração);
- Ignora entradas inválidas com mensagens de erro amigáveis;
- Aloca aulas nos turnos da manhã (09:00–12:00) e tarde (13:00–17:00);
- Impede que o mesmo professor dê duas aulas no mesmo horário;
- Reserva todos os dias úteis com uma reunião às 17:00.

---

## 📌 Exemplo de saída

```
📅 Segunda-feira
09:00 Matemática - Prof. Silva 50 min
09:50 História - Prof. Almeida 60 min
10:50 Filosofia - Prof. Cunha 30 min
13:00 Informática - Prof. Teixeira 50 min
14:00 Física - Prof. Lima 45 min
17:00 Reunião de professores (obrigatória)
📅 Terça-feira
...
```

