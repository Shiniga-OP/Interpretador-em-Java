import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Interpretador {

    private Map<String, Object> variaveis = new HashMap<>();
    private Map<String, String> funcoes = new HashMap<>();

    public void interpretar(String codigo) {
        String codigoSF = this.declararFuncoes(codigo);
        String[] linhas = codigoSF.trim().split("\n");

        for(String linha : linhas) {
            linha = linha.trim();
            if (linha.isEmpty() || linha.startsWith("//")) {
                continue;
            }

            if(linha.startsWith("log(")) {
                String conteudo = linha.substring(linha.indexOf("(") + 1, linha.lastIndexOf(")")).trim();

                if(variaveis.containsKey(conteudo)) {
                    System.out.println(variaveis.get(conteudo));
                } else {
                    if(conteudo.startsWith("\"") && conteudo.endsWith("\"")) {
                        System.out.println(conteudo.substring(1, conteudo.length() - 1));
                    } else {
                        System.out.println(conteudo);
                        }
                    }
                } else if(linha.startsWith("var ")) {
                    this.declararVar(linha);
                } else if(linha.contains(");")){
                    this.executarF(linha.replace("();", ""));
                } else {
                System.err.println("comando não reconhecido: " + linha);
            }
        }
    }
    
    public void executarF(String funcao) {
        if(funcoes.containsKey(funcao)) {
            this.interpretar(funcoes.get(funcao));
        } else {
            System.err.println("erro ao executara função:"+funcao);
        }
    }

    public void declararVar(String linha) {
        String[] partes = linha.substring(4).split("=");
        if(partes.length != 2) {
            System.err.println("erro de sintaxe em definição de variável na linha: " + linha);
            return;
        }

        String nomeVar = partes[0].trim();
        String valorStr = partes[1].trim().replace(";", "").replace("\"", "");

        Object valor;
        try {
            valor = Integer.parseInt(valorStr);
        } catch (NumberFormatException e) {
            try {
                valor = Double.parseDouble(valorStr);
            } catch (NumberFormatException e2) {
                valor = valorStr;
            }
        }
        variaveis.put(nomeVar, valor);
    }

    public String declararFuncoes(String codigo) {
        String padrao = "funcao\\s+(\\w+)\\(\\)\\s+\\{([\\s\\S]*?)\\}";

        Pattern capturar = Pattern.compile(padrao);
        Matcher funcao = capturar.matcher(codigo);

        while(funcao.find()) {
            funcoes.put(funcao.group(1), funcao.group(2).trim());
        }
        
        return funcao.replaceAll(" ");
    }
}
