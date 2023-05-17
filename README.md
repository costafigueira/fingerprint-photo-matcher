# Fingerprint Photo Matcher

Projeto para identificação de pessoas por impressões digitais extraídas por fotos, sejam elas de câmeras fotográficas profissionais ou smartphones.

# Como funciona?

O Fingerprint Photo Matcher ao receber uma foto faz o processamento da mesma, a fim de se obter uma biometria equivalente as extraídas por leitoras ópticas especializadas, e após isso utiliza um extrator e matcher via API REST para comparar duas biometrias. Por default está sendo utilizado o extrator e matcher da Nerotechnology implementado através de uma API da BRy Tecnologia.

# Como rodar a aplicação?
Para subir a API pode ser utilizado o docker e docker-compose disponibilizado na raiz do projeto, por meio do comando

	docker compose up -d

Ou rodar localmente a aplicação Java Spring com Maven, tendo instalado Java 8 e Maven 3.6.1. Com todas as dependências instaladas, executar a partir da raiz do projeto:

	mvn clean install -DskipTests
	java -jar target/fingerprint-photo-matcher-0.0.1-SNAPSHOT.jar

# Como utilizar?
O Fingerprint Photo Matcher após executado pode ser utilizado via API que possui uma documentação Postman presente em /src/resources/api.

Ou

Executar testes JUnit presentes em /src/test/ que irá utilizar as biometrias inseridas em /src/test/resources. Nos testes as biometrias inseridas em /src/test/resources/images são fotos dos dedos que serão processadas com o algoritmo de processamento de imagens proposto neste trabalho. Já as biometrias inseridas em /src/test/resources/wsqs são arquivos .wsq extraídos de uma leitora óptica especialista.

As biometrias inseridas em /src/test/resources/ seguem o seguinte formato:
A_1, A_2, A_3, B_1, B_2, B_3, ...

Onde as letras maiúsculas no início do nome refere-se ao dedo da foto com a tradução:

	"A" = LEFT_HAND_PINKY;
	"B" = LEFT_HAND_RING;
	"C" = LEFT_HAND_MIDDLE;
	"D" = LEFT_HAND_INDEX;
	"E" = LEFT_HAND_THUMB;
	"F" = RIGHT_HAND_THUMB;
	"G" = RIGHT_HAND_INDEX;
	"H" = RIGHT_HAND_MIDDLE;
	"I" = RIGHT_HAND_RING;
	"J" = RIGHT_HAND_PINKY;

E os números após o _ é um contador de quantas biometrias pertencem ao mesmo dedo.

Após a execução dos testes os resultados estão presentes em /src/test/target onde estarão presentes as pastas:
 - postman-env: Pasta onde estará o json com as envs Postman gerado a partir das biometrias inseridas na pasta /src/test/resource (seguindo o mesmo formato de nomes);
 
 - processed: Pasta que conterá tanto as biometrias extraidas de fotos quanto os WSQs processados pelo algoritmo de processamento e pelo extrator;
 
 - result: Pasta que irá conter todos os testes de match realizado com três subpastas: image-image (match de todos os dedos extraídos de imagens entre si), image-wsq (match de todos os dedos extraídos de imagens com todos os dedos extraídos de WSQs) e wsq-wsq (match de todos os dedos extraídos de WSQs entre si);
 
 - reports: Pasta que irá conter o relatório dos testes de macth.
