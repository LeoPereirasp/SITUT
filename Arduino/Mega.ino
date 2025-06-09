unsigned long duracao = 0;      // Duracao em minutos
unsigned long intervalo = 0;     // Intervalo em horas
unsigned long duracao_m = 0;    // Duracao em milissegundos
unsigned long intervalo_h = 0;   // Intervalo em milissegundos

String modo = "auto";  // Variar entre "auto" e "manu"
int valorumi = 500; // valor do sensor de umidade

unsigned long tempoIrrigacaoInicio = 0;  // Marcação do tempo de início da irrigação
bool irrigando = false;  // Flag para verificar se está irrigando
unsigned long tempoIntervaloInicio = 0;  // Marcador do início do intervalo
bool irrigacaoFinalizada = false; // Flag para saber se a irrigação foi concluída
bool aguardandoIntervalo = false; // Flag para saber se está aguardando o intervalo

void setup() {
  pinMode(A1, INPUT);  // Sensor de chuva
  pinMode(A3, INPUT);  // Sensor UV
  pinMode(A5, INPUT);  // Sensor de umidade
  pinMode(2, OUTPUT);  // Relé ou válvula (controle da irrigação)
  pinMode(3, OUTPUT);  // Indicação de irrigação no NodeMCU
  

  Serial.begin(9600);  // Comunicação serial com o computador
  Serial1.begin(9600); // Comunicação serial com o NodeMCU
}

void loop() {
  // Enviar dados continuamente, sem bloquear o fluxo
  enviarDadosSensores();
  
  verificarDI();  // Verifica e atualiza o modo com base no Firebase

  // Se o modo for "auto", fazemos o controle automático de irrigação
  if (modo == "auto") {
    irrigacaoAuto();
  } else {  // Modo manual, aguarda dados do Firebase
    irrigacaoManual();
  }

  // Controle da porta 3 no NodeMCU baseado no estado da irrigação
  controleNodeMCU();
}

// Função para enviar dados dos sensores
void enviarDadosSensores() {
  Serial1.println(analogRead(A1));  // Envia dados do sensor de chuva
  delay(50);
  Serial1.println(analogRead(A5));  // Envia dados do sensor de umidade
  delay(50);
  Serial1.println(analogRead(A3));  // Envia dados do sensor UV
  delay(1000);
}

// Função para irrigação manual
void irrigacaoManual() {
  if (modo == "manu" && duracao > 0 && intervalo > 0) {  // Se dados de duração e intervalo foram recebidos
    // Inicia a irrigação manual com base na duração
    if (!irrigando && !aguardandoIntervalo) {  // Certifique-se de que não está aguardando o intervalo
      digitalWrite(2, LOW);  // Liga a irrigação (rele ou válvula)
      Serial.println("Irrigação Manual Ativa");
      tempoIrrigacaoInicio = millis();  // Marca o início da irrigação
      irrigando = true;  // Irrigação iniciada
    }

    // Tempo de irrigação
    unsigned long tempoDecorrido = millis() - tempoIrrigacaoInicio;
    Serial.print("Tempo decorrido (Irrigação): ");
    Serial.println(tempoDecorrido); // Exibe o tempo decorrido da irrigação

    // Verifica se o tempo de irrigação terminou
    if (irrigando && tempoDecorrido >= duracao_m) {  // Comparando tempo decorrido com a duração (em milissegundos)
      digitalWrite(2, HIGH);  // Desliga a irrigação
      Serial.println("Irrigação Manual concluída.");
      irrigando = false;  // Reseta o estado de irrigação
      aguardandoIntervalo = true; // Agora estamos esperando o intervalo passar
      tempoIntervaloInicio = millis();  // Marca o início do intervalo
      Serial.print("Intervalo iniciado em: ");
      Serial.println(tempoIntervaloInicio); // Exibe quando o intervalo começa
    }

    // Verifica se está aguardando o intervalo
    if (aguardandoIntervalo) {
      unsigned long tempoIntervaloDecorrido = millis() - tempoIntervaloInicio; // Tempo de intervalo
      Serial.print("Tempo de intervalo decorrido: ");
      Serial.println(tempoIntervaloDecorrido); // Exibe o tempo decorrido do intervalo

      // Se o intervalo de tempo passar
      if (tempoIntervaloDecorrido >= intervalo_h) {
        Serial.println("Intervalo concluído. Pronto para nova irrigação manual.");
        aguardandoIntervalo = false;  // Permite reiniciar a irrigação
        modo = "auto";  // Volta para o modo automático
        Serial.println("Voltando para modo automático.");
      }
    }
  } else {
    if (modo != "manu") {
      Serial.println("Modo não está manual, irrigação automática.");
      modo = "auto";  // Se não há dados de duração e intervalo, volta para o modo automático
    } else {
      Serial.println("Dados de duração e intervalo não recebidos. Irrigação no modo automático.");
      modo = "auto";  // Se não houver dados de duração e intervalo, volta para o modo automático
    }
  }
}

// Função para irrigação automática
void irrigacaoAuto() {
  // Lógica de irrigação automática baseada nos sensores

  // Verifica se está chovendo (A1 é o sensor de chuva)
  if (analogRead(A1) < 850) {  // Verifica se o valor do sensor de chuva é menor que 850 (indicando chuva)
    digitalWrite(2, HIGH);  // Desativa a irrigação
    Serial.println("Chovendo. Irrigação desativada.");
  } 
  // Se o solo estiver seco, ativa a irrigação
  else if (analogRead(A5) > valorumi) {  // Se o valor do sensor de umidade do solo (A5) for menor que o valorumi
    if (!irrigando) {
      digitalWrite(2, LOW);  // Ativa a irrigação
      Serial.println("Irrigação Automática Ativa");

      // Inicia a irrigação (não usando millis() para controle de tempo, a irrigação fica ativa até as condições mudarem)
      tempoIrrigacaoInicio = millis();
      irrigando = true;
    }
  } else {
    // Se o solo já está úmido, desativa a irrigação
    digitalWrite(2, HIGH);  // Desativa a irrigação
    Serial.println("Solo úmido. Irrigação desativada.");
  }

  // Verifica se a irrigação deve ser desligada
  if (irrigando && (analogRead(A1) < 850 || analogRead(A5) <= valorumi)) {
    digitalWrite(2, HIGH);  // Desativa a irrigação
    Serial.println("Irrigação Automática concluída.");
    irrigando = false;
  }
}



// Função para verificar os dados recebidos do Firebase
void verificarDI() {
  if (Serial1.available() > 0) {
    String receivedData = Serial1.readStringUntil('\n');  // Lê até o fim da linha
    receivedData.trim();

    // Exemplo de formato esperado: "DURACAO:53;INTERVALO:120;"
    if (receivedData.startsWith("Enviando Duracao:")) {
      duracao = receivedData.substring(18).toInt();  // Extrai a duração
      Serial.print("Duração recebida: ");
      Serial.println(duracao);
      duracao_m = duracao * 60000;  // Converte para milissegundos
      modo = "manu";  // Muda para modo manual
    } else if (receivedData.startsWith("Enviando Intervalo:")) {
      intervalo = receivedData.substring(19).toInt();  // Extrai o intervalo
      Serial.print("Intervalo recebido: ");
      Serial.println(intervalo);

      intervalo_h = intervalo * 3600000;  // Converte para milissegundos (intervalo em horas)
      Serial.print("Intervalo em milissegundos: ");
      Serial.println(intervalo_h); 
    }
  }
}

// Função para controlar a porta 3 no NodeMCU
void controleNodeMCU() {
  if (digitalRead(2) == LOW) {
    digitalWrite(3, HIGH);  // Se a irrigação estiver ligada, porta 3 estará LOW
  } else {
    digitalWrite(3, LOW);  // Se a irrigação estiver desligada, porta 3 estará HIGH
  }
}
