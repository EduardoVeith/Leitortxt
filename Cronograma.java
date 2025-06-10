import java.io.*;
import java.util.*;
import java.util.regex.*;

/**
 * Classe principal responsável por montar o cronograma de aulas evitando
 * conflitos de horário entre professores e respeitando a reunião das 17h.
 */
public class Cronograma {

    public static void main(String[] args) {
        // Caminho do arquivo contendo os dados das aulas
        String caminhoArquivo = "aulas.txt";

        // Carrega as aulas a partir do arquivo de texto
        List<Aula> aulas = carregarAulas(caminhoArquivo);

        // Verifica se alguma aula foi carregada com sucesso
        if (aulas.isEmpty()) {
            System.out.println("Nenhuma aula válida encontrada.");
            return;
        }

        // Gera o cronograma com base nas aulas válidas e imprime no console
        List<String> cronograma = gerarCronograma(aulas);
        cronograma.forEach(System.out::println);
    }

    /**
     * Método responsável por ler o arquivo de entrada (aulas.txt),
     * validar e transformar cada linha válida em um objeto Aula.
     */
    private static List<Aula> carregarAulas(String caminho) {
        List<Aula> aulas = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha;
            int numeroLinha = 1;

            while ((linha = br.readLine()) != null) {
                linha = linha.trim();

                // Ignora linhas vazias
                if (linha.isEmpty()) {
                    numeroLinha++;
                    continue;
                }

                // Verifica se a linha possui o separador '-'
                if (!linha.contains("-")) {
                    System.err.printf("❌ Linha %d ignorada: não contém separador '-': \"%s\"%n", numeroLinha, linha);
                    numeroLinha++;
                    continue;
                }

                // Divide a linha em duas partes: nome da aula e info do professor
                String[] partes = linha.split("-");
                if (partes.length < 2) {
                    System.err.printf("❌ Linha %d ignorada: formato inválido (esperado: Aula - Prof. Nome Duração)%n",
                            numeroLinha);
                    numeroLinha++;
                    continue;
                }

                String nomeAula = partes[0].trim();
                String infoProfessor = partes[1].trim();

                // Verifica se o campo do professor está vazio
                if (infoProfessor.isEmpty()) {
                    System.err.printf("❌ Linha %d ignorada: nome do professor ausente.%n", numeroLinha);
                    numeroLinha++;
                    continue;
                }

                // Extrai o nome do professor
                String[] palavras = infoProfessor.split("\\s+");
                String nomeProfessor = palavras.length >= 2 ? palavras[1] : palavras[0];

                // Extrai a duração da aula usando expressão regular
                Matcher matcher = Pattern.compile("-?\\d+").matcher(infoProfessor);
                int duracao = 0;
                boolean duracaoValida = false;

                if (matcher.find()) {
                    try {
                        duracao = Integer.parseInt(matcher.group());
                        duracaoValida = duracao > 0;
                    } catch (NumberFormatException e) {
                        duracaoValida = false;
                    }
                }

                // Verifica se a duração é válida
                if (!duracaoValida) {
                    System.err.printf("❌ Linha %d ignorada: duração inválida ou não positiva: \"%s\"%n", numeroLinha,
                            linha);
                    numeroLinha++;
                    continue;
                }

                // Caso esteja tudo certo, adiciona aula à lista
                aulas.add(new Aula(nomeAula, nomeProfessor, duracao));
                numeroLinha++;
            }

        } catch (IOException e) {
            // Trata erro de leitura do arquivo
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        }

        return aulas;
    }

    /**
     * Método que gera o cronograma semanal.
     * Ele percorre os dias úteis e aloca as aulas em dois turnos: manhã e tarde.
     * Ao final de cada dia, fixa a reunião de professores às 17h.
     */
    private static List<String> gerarCronograma(List<Aula> aulas) {
        List<String> cronograma = new ArrayList<>();
        String[] diasSemana = { "Segunda-feira", "Terça-feira", "Quarta-feira", "Quinta-feira", "Sexta-feira" };

        // Agenda que armazena os horários ocupados por professor
        Map<String, List<Intervalo>> agendaProfessores = new HashMap<>();

        int indiceAula = 0;

        for (String dia : diasSemana) {
            cronograma.add("📅 " + dia);

            // Tenta alocar aulas no turno da manhã (9h–12h)
            indiceAula = alocarTurno(aulas, cronograma, indiceAula, 9, 12, agendaProfessores);

            // Tenta alocar aulas no turno da tarde (13h–17h)
            indiceAula = alocarTurno(aulas, cronograma, indiceAula, 13, 17, agendaProfessores);

            // Adiciona a reunião obrigatória ao final do dia
            cronograma.add("17:00 Reunião de professores (obrigatória)");

            // Se não há mais aulas, finaliza
            if (indiceAula >= aulas.size()) {
                break;
            }
        }

        return cronograma;
    }

    /**
     * Método que tenta alocar as aulas dentro de um intervalo de tempo (turno).
     * Ele garante que o professor não tenha duas aulas ao mesmo tempo
     * e respeita o fim do turno (ex: até 12h ou até 17h).
     *
     * @param aulas       lista de aulas válidas
     * @param cronograma  lista de strings do cronograma
     * @param indiceAtual posição da próxima aula a ser considerada
     * @param horaInicio  hora inicial do turno (ex: 9 ou 13)
     * @param horaFim     hora final do turno (ex: 12 ou 17)
     * @param agenda      mapa que controla horários ocupados por professor
     * @return novo índice após tentar alocar as aulas do turno
     */
    private static int alocarTurno(List<Aula> aulas, List<String> cronograma, int indiceAtual,
            int horaInicio, int horaFim, Map<String, List<Intervalo>> agenda) {

        int horaAtual = horaInicio;
        int minutoAtual = 0;
        int fimTurnoMinutos = horaFim * 60;

        while (indiceAtual < aulas.size()) {
            Aula aula = aulas.get(indiceAtual);

            // Converte o horário atual em minutos desde 00:00
            int inicio = horaAtual * 60 + minutoAtual;
            int fim = inicio + aula.duracaoMin;

            // Se a aula não cabe no turno, para
            if (fim > fimTurnoMinutos) {
                break;
            }

            // Cria o intervalo de tempo da aula
            Intervalo novoHorario = new Intervalo(inicio, fim);

            // Verifica a agenda do professor
            List<Intervalo> horariosOcupados = agenda.getOrDefault(aula.professor, new ArrayList<>());

            // Verifica se há conflito com outra aula já alocada
            boolean conflito = horariosOcupados.stream().anyMatch(h -> h.conflitaCom(novoHorario));

            if (!conflito) {
                // Formata e adiciona a aula ao cronograma
                String horarioFormatado = String.format("%02d:%02d", horaAtual, minutoAtual);
                cronograma.add(horarioFormatado + " " + aula.aula + " - " + "Prof. " + aula.professor + " "
                        + aula.duracaoMin + " min");

                // Atualiza a agenda do professor com o novo horário
                horariosOcupados.add(novoHorario);
                agenda.put(aula.professor, horariosOcupados);

                // Avança o tempo do turno
                minutoAtual += aula.duracaoMin;
                horaAtual += minutoAtual / 60;
                minutoAtual %= 60;

                // Passa para a próxima aula
                indiceAtual++;
            } else {
                // Pula essa aula (devido a conflito) e tenta a próxima
                indiceAtual++;
            }
        }

        return indiceAtual;
    }
}
