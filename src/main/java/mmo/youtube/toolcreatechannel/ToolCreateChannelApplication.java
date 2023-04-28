package mmo.youtube.toolcreatechannel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ToolCreateChannelApplication {
	public static final String filePath = "gmail.txt";
	public static final int numberThreads = 10;

	public static void main(String[] args) throws IOException, InterruptedException {
		SpringApplication.run(ToolCreateChannelApplication.class, args);
		// Khởi tạo ChromeDriver
		System.setProperty("webdriver.chrome.driver", "chromedriver_win32\\chromedriver.exe");
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--remote-allow-origins=*");
		options.addArguments("--disable-notifications");

		// Đọc file chứa email và mật khẩu
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		String line;

		List<String> data = new ArrayList<String>();
		// Tạo ThreadPool với kích thước 5
		ExecutorService executorService = Executors.newFixedThreadPool(numberThreads);

		while ((line = br.readLine()) != null) {
			line = line.replaceAll("\\s+", " "); // xóa các khoảng trắng dư thừa và giữ lại một dấu cách
			String[] parts = line.split(" "); // tách email và password ra từ chuỗi dữ liệu
			String email = parts[0];
			String password = parts[1];
			String emailRecovery = parts[2];

			// Tạo một Runnable mới với email và mật khẩu tương ứng
			Runnable worker = new WorkerThread(email, password, emailRecovery, options, data);
			executorService.execute(worker);
		}
		br.close();
	}

}

class WorkerThread implements Runnable {
	private final String email;
	private final String password;
	private final String emailRecovery;
	private final ChromeOptions options;
	private final List<String> data;
	public final int fiveSeconds = 5000;
	public final int tenSeconds = 10000;
	public final int threeseconds = 3000;

	public WorkerThread(String email, String password, String emailRecovery, ChromeOptions options, List<String> data) {
		this.email = email;
		this.password = password;
		this.emailRecovery = emailRecovery;
		this.options = options;
		this.data = data;
	}

	@Override
	public void run() {
		WebDriver driver = new ChromeDriver(options);
		Duration duration = Duration.ofSeconds(300);
		WebDriverWait wait = new WebDriverWait(driver, duration);
		try {
			driver.get("https://www.youtube.com/");
			try {
				WebElement signIn = wait.until(ExpectedConditions
						.presenceOfElementLocated(By.xpath("//a[@aria-label='Đăng nhập' or @aria-label='Sign in']")));
				signIn.click();
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage() + ". " + e.getCause());
				String st = email + " " + password + " " + emailRecovery;
				data.add(st);
				driver.quit();
			}

			// Điền email và mật khẩu vào các phần tử input tương ứng
			try {
				WebElement emailInput = wait
						.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@type='email']")));
				emailInput.sendKeys(email);
				WebElement nextButton = driver.findElement(By.xpath("//div[@id='identifierNext']"));
				nextButton.click();
				Thread.sleep(tenSeconds); // đợi 5 giây
				WebElement passwordInput = driver.findElement(By.xpath("//input[@type='password']"));
				passwordInput.sendKeys(password);
				WebElement signInButton = driver.findElement(By.xpath("//div[@id='passwordNext']"));
				signInButton.click();
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage() + ". " + e.getCause());
				String st = email + " " + password + " " + emailRecovery;
				data.add(st);
				driver.quit();
			}

			try {
				Thread.sleep(fiveSeconds); // đợi 5 giây
				WebElement confirm = driver.findElement((By.xpath("//input[@id='confirm']")));
				confirm.click();
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage() + ". " + e.getCause());
			}

//			try {
//				Thread.sleep(fiveSeconds); // đợi 5 giây
//				WebElement confirmRecoveryEmail = driver.findElement((By.xpath(
//						"//div[contains(text(), 'Xác nhận email khôi phục của bạn') or contains(text(), 'Confirm your recovery email')]")));
//				confirmRecoveryEmail.click();
//				Thread.sleep(fiveSeconds); // đợi 5 giây
//				WebElement emailRecoveryEmail = driver.findElement(By.xpath("//input[@type='email']"));
//				emailRecoveryEmail.sendKeys(emailRecovery);
//				List<WebElement> nextButton = driver.findElements(By.xpath("//button[@type='button']"));
//				nextButton.get(0).click();
//			} catch (Exception e) {
//				System.out.println("Error: " + e.getMessage() + ". " + e.getCause());
//			}

			try {
				WebElement avatarBtn = wait
						.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@id='avatar-btn']")));
				avatarBtn.click();
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage() + ". " + e.getCause());
				String st = email + " " + password + " " + emailRecovery;
				data.add(st);
				driver.quit();
			}

			Thread.sleep(tenSeconds); // đợi 5 giây
			try {
				WebElement subtitle = driver.findElement(By.xpath(
						"//yt-formatted-string[contains(text(), 'Tạo kênh') or contains(text(), 'Create a channel')][@id='label']"));
				subtitle.click();
				Thread.sleep(tenSeconds); // đợi 10 giây
				WebElement createChannel = driver
						.findElement(By.xpath("//button[@aria-label='Tạo kênh' or @aria-label='Create channel']"));
				createChannel.click();
				Thread.sleep(tenSeconds); // đợi 10 giây
				System.out.println("Successfully created channel with Email: " + email);
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage() + ". " + e.getCause());
				driver.quit();
			}
			driver.quit();
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage() + ". " + e.getCause());
			String st = email + " " + password + " " + emailRecovery;
			data.add(st);
			driver.quit();
		} finally {
			Set<String> setWithoutDuplicates = new HashSet<>(data);
			List<String> masterData = new ArrayList<>(setWithoutDuplicates);
			String fileName = "gmail_error.txt";

			try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
				for (String entry : masterData) {
					bw.write(entry);
					bw.newLine();
				}
			} catch (IOException e) {
				e.printStackTrace();
				driver.quit();
			}
		}

	}
}
