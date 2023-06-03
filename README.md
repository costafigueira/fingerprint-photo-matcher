# Fingerprint Photo Matcher

Projeto para identificação de pessoas por impressões digitais extraídas por fotos, sejam elas de câmeras fotográficas profissionais ou smartphones.

# Como funciona?

O Fingerprint Photo Matcher ao receber uma foto realiza o processamento da imagem, a fim de se obter uma biometria equivalente as extraídas por leitoras ópticas especializadas, e após isso utiliza um extrator e matcher via API REST para comparar duas biometrias. Por default está sendo utilizado o extrator e matcher da Nerotechnology implementado através de uma API da BRy Tecnologia.

# Como rodar a aplicação?
Para subir a API pode ser utilizado o docker e docker-compose disponibilizado na raiz do projeto, por meio do comando

	docker compose up -d

Ou rodar localmente a aplicação Java Spring com Maven, tendo instalado Java 17 e Maven 3.6.3. Os testes serão executados automaticamente. para iniciar a execução basta executar os seguintes comandos a partir da raiz do projeto:

	mvn clean install
	java -jar target/fingerprint-photo-matcher-1.0.0.jar

# Como utilizar?
O Fingerprint Photo Matcher após executado pode ser utilizado via API que possui uma documentação Postman presente em /src/resources/api.

Ou

Executar testes JUnit presentes em /src/test/ que irá utilizar as biometrias inseridas em /src/test/resources. Nos testes as biometrias inseridas em /src/test/resources/images são fotos dos dedos, que serão processadas com o algoritmo de processamento de imagens proposto neste trabalho. Já as biometrias inseridas em /src/test/resources/wsqs são arquivos .wsq extraídos de uma leitora óptica especialista.

As biometrias inseridas em /src/test/resources/ seguem o seguinte formato:
**nome_do_dedo-numero**

Os possíveis dedos são:

	LEFT_HAND_PINKY
	LEFT_HAND_RING
	LEFT_HAND_MIDDLE
	LEFT_HAND_INDEX
	LEFT_HAND_THUMB
	RIGHT_HAND_THUMB
	RIGHT_HAND_INDEX
	RIGHT_HAND_MIDDLE
	RIGHT_HAND_RING
	RIGHT_HAND_PINKY

E os números após o - é um contador de quantas biometrias pertencem ao mesmo dedo.

Após a execução dos testes os resultados podem ser encontrados em /src/test/target onde estarão presentes as pastas:
 - postman-env: Pasta contendo o json com as envs Postman gerado a partir das biometrias inseridas na pasta /src/test/resource (seguindo o mesmo formato de nomes);
 
 - processed: Pasta contendo tanto as biometrias extraidas de fotos quanto os WSQs processados pelo algoritmo de processamento e pelo extrator;
 
 - result: Pasta contendo todos os testes de match realizado com três subpastas: image-image (match de todos os dedos extraídos de imagens entre si), image-wsq (match de todos os dedos extraídos de imagens com todos os dedos extraídos de WSQs) e wsq-wsq (match de todos os dedos extraídos de WSQs entre si);
 
 - reports: Pasta contendo o relatório dos testes de match com as taxas de FAR, FRR, TAR, FAR e EER obtidas.
