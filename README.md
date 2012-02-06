simple-sqs
===================
（執筆中）
simple-sqsは、Amazon SQS（Simple Queue Service）を使った非同期ジョブ実行を簡易に実装するためのフレームワークです。

基本的には、AbstractJobTicketという抽象クラスを継承したサブクラスを実装します。これは、キューからメッセージを受け取った際の実際のジョブロジックを実装するためのクラスになります。また、ジョブ管理のためにSimpleDBに永続化されますので、メッセージの二重受信などのSQS特有の問題に煩わされる事がありません。

以下はサンプルコードです。

```java
@SQSJobTicket(queueName = "SimpleSQS-HelloJobTicket")
@SimpleDBEntity(domainName = "SimpleSQS-HelloJobTicket", s3BucketName = "dateofrock-testing", s3KeyPrefix = "SimpleSQS-HelloJobTicket")
public class HelloJobTicket extends AbstractJobTicket {

	@SimpleDBAttribute(attributeName = "userName")
	public String userName;

	@Override
	public void prepare() throws Exception {
		String[] presenResult = { "スベった", "ドン引きされた", "感動巨編だった" };
		Random rand = new Random();
		int idx = rand.nextInt(presenResult.length);
		this.userName = presenResult[idx];
		System.out.println(String.format("プレゼン結果を準備しました...", this.userName));
	}

	@Override
	public void execute() throws Exception {
		System.out.println(String.format("あなたのプレゼンは、「%s」ようです。", this.userName));
		Thread.sleep(1000);
	}

	@Override
	public void processIfInProgress() {
		// TODO
	}

}
```


Install
----
Mavenのリポジトリを用意してありますので、pom.xmlに以下の記述を追加してください。

```xml
<repositories>
	<repository>
		<id>dateofrock</id>
		<url>https://s3-ap-northeast-1.amazonaws.com/dateofrock-repository/maven/</url>
	</repository>
</repositories>
<dependencies>
	<dependency>
		<groupId>com.dateofrock.aws</groupId>
		<artifactId>simple-sqs</artifactId>
		<version>0.1-SNAPSHOT</version>
	</dependency>
</dependencies>
```

なお、simple-sqsは[simpledb-mapper](https://github.com/dateofrock/simpledb-mapper)と、AWS SDK for Javaライブラリを利用しています。


Limitation
==============
* 執筆中

Licence
==============
* ソースコードはApache Licence 2.0とし、すべてをgithub上に公開する事とします。（[https://github.com/dateofrock/simpledb-mapper](https://github.com/dateofrock/simpledb-mapper)）
* 当ソフトウェアの動作は保証しません。ご自身の責任と判断でご利用ください。
* 当ソフトウェアを利用することにより発生したいかなる損害も当方は責任を負わないものとします。



Author
==============

Takehito Tanabe - [dateofrock](http://blog.dateofrock.com/)