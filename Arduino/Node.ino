#include <ESP8266WiFi.h>
#include <FirebaseESP8266.h>

// Configurações do Firebase
#define FIREBASE_HOST "situt-258258-default-rtdb.firebaseio.com" // Substitua pelo ID do seu projeto
#define FIREBASE_AUTH "OwjXrP76U9ed8JMzJyADzeQ9sMEPvVS1InJqBNi3"           // Substitua pela chave secreta do banco de dados

// Configurações da rede WiFi
const char* ssid = "Zack";
const char* password = "28022007";

// Inicializa o cliente Firebase
FirebaseData firebaseData;
FirebaseConfig firebaseConfig;
FirebaseAuth firebaseAuth;

String chuva = "";
String umidade = "";
String uv = "";
String irrigando = ""; // Declara a variável irrigando
int duracao = 0; 
int intervalo = 0;
int lastDuracao = -1;
int lastIntervalo = -1;

unsigned long previousMillis = 0; // Armazena o último tempo de envio
const long interval = 500; // Intervalo de envio dos dados do Firebase (500 ms)

void setup() {
  Serial.begin(9600);
  pinMode(D3, INPUT);
  

  // Conectar ao WiFi
  WiFi.begin(ssid, password);
  Serial.println("Conectando ao WiFi...");
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("Conectado ao WiFi!");
  Serial.print("Endereço IP: ");
  Serial.println(WiFi.localIP());

  // Configurar Firebase
  Serial.println("Configurando Firebase...");
  firebaseConfig.database_url = FIREBASE_HOST;
  firebaseConfig.signer.tokens.legacy_token = FIREBASE_AUTH;

  Firebase.begin(&firebaseConfig, &firebaseAuth);
  Firebase.reconnectWiFi(true);

  // Verificar a conexão inicial com o Firebase
  if (Firebase.ready()) {
    Serial.println("Conectado ao Firebase!");
  } else {
    Serial.println("Falha ao conectar ao Firebase: " + firebaseData.errorReason());
  }
}

void loop() {
  // Envia dados recebidos do Arduino Mega para o Firebase
  if (Serial.available()) {
    String chuva = Serial.readStringUntil('\n'); // Lê o valor da chuva
    String umidade = Serial.readStringUntil('\n'); // Lê o valor da umidade
    String uv = Serial.readStringUntil('\n'); // Lê o valor do UV

    // Criar um objeto JSON
    FirebaseJson json;
    json.set("chuva", chuva);
    json.set("umidade", umidade);
    json.set("uv", uv);
    Firebase.setJSON(firebaseData, "/sensores", json);
  }

  int estadoD3 = digitalRead(D3);

  if (estadoD3 == HIGH) {
    irrigando = "Sim";
  } else {
    irrigando = "Não";
  }
  // Criar um objeto JSON para enviar o status de irrigação
  FirebaseJson jsonStatus;
  jsonStatus.set("Irrigando", irrigando);
  Firebase.setJSON(firebaseData, "/status", jsonStatus);  
  

  // Verifica se é hora de enviar os dados do Firebase
  unsigned long currentMillis = millis();
  if (currentMillis - previousMillis >= interval) {
    previousMillis = currentMillis; // Atualiza o último tempo de envio

    lerDadosFirebase(); // Lê os dados do Firebase e envia ao Mega
  }
}

void lerDadosFirebase() {
  if (Firebase.getInt(firebaseData, "/Irriga/Duracao")) {
    duracao = firebaseData.intData();
    if (duracao != lastDuracao) {
      Serial1.println(duracao); // Envia apenas o valor de duração
      lastDuracao = duracao;
      Serial.println("Enviando Duracao: " + String(duracao)); // Mensagem de depuração
    }
  }

  if (Firebase.getInt(firebaseData, "/Irriga/Intervalo")) {
    intervalo = firebaseData.intData();
    if (intervalo != lastIntervalo) {
      Serial1.println(intervalo); // Envia apenas o valor de intervalo
      lastIntervalo = intervalo;
      Serial.println("Enviando Intervalo: " + String(intervalo)); // Mensagem de depuração
    }
  }
}