package mmo.youtube.toolcreatechannel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ToolCreateChannelApplication {
	public static final int fiveSeconds = 3000;
	public static final int tenSeconds = 8000;
	public static final int threeeconds = 2500;
	public static final String filePath = "gmail.txt";

	public static void main(String[] args) throws IOException, InterruptedException {
		SpringApplication.run(ToolCreateChannelApplication.class, args);
		// Khởi tạo ChromeDriver
		System.setProperty("webdriver.chrome.driver", "chromedriver_win32\\chromedriver.exe");
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--remote-allow-origins=*");
		options.addArguments("--disable-notifications");
		WebDriver driver = new ChromeDriver(options);

		// Đọc file chứa email và mật khẩu
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		String line;

		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
		String fileName = "gmail_error_" + now.format(formatter) + ".txt";

		List<String> data = new ArrayList<String>();

		while ((line = br.readLine()) != null) {
			line = line.replaceAll("\\s+", " "); // xóa các khoảng trắng dư thừa và giữ lại một dấu cách
			String[] parts = line.split(" "); // tách email và password ra từ chuỗi dữ liệu
			String email = parts[0];
			String password = parts[1];

			driver.get("https://www.youtube.com/");
			Thread.sleep(tenSeconds); // đợi 10 giây

			WebElement signIn = driver.findElement(By.xpath("//a[@aria-label='Đăng nhập' or @aria-label='Sign in']"));
			signIn.click();

			Thread.sleep(fiveSeconds); // đợi 5 giây
			// Điền email và mật khẩu vào các phần tử input tương ứng
			try {
				WebElement emailInput = driver.findElement(By.xpath("//input[@type='email']"));
				emailInput.sendKeys(email);
				WebElement nextButton = driver.findElement(By.xpath("//div[@id='identifierNext']"));
				nextButton.click();
				Thread.sleep(fiveSeconds); // đợi 5 giây
				WebElement passwordInput = driver.findElement(By.xpath("//input[@type='password']"));
				passwordInput.sendKeys(password);
				WebElement signInButton = driver.findElement(By.xpath("//div[@id='passwordNext']"));
				signInButton.click();
			} catch (Exception e) {
				try {
					System.out.println("Error: " + e.getMessage() + ". " + e.getCause());
					WebElement anotherAccount = driver.findElement(By.xpath(
							"//div[contains(text(), 'Sử dụng một tài khoản khác') or contains(text(), 'Use another account')]"));
					anotherAccount.click();
					Thread.sleep(fiveSeconds); // đợi 5 giây
					WebElement emailInput = driver.findElement(By.xpath("//input[@type='email']"));
					emailInput.sendKeys(email);
					WebElement nextButton = driver.findElement(By.xpath("//div[@id='identifierNext']"));
					nextButton.click();
					Thread.sleep(fiveSeconds); // đợi 5 giây
					WebElement passwordInput = driver.findElement(By.xpath("//input[@type='password']"));
					passwordInput.sendKeys(password);
					WebElement signInButton = driver.findElement(By.xpath("//div[@id='passwordNext']"));
					signInButton.click();
				} catch (Exception e2) {
					String st = email + " " + password;
					data.add(st);
					System.out.println("Error: " + e.getMessage() + ". " + e.getCause());
					continue;
				}
			}

			// Đợi một chút để đảm bảo các hành động trước đó đã hoàn thành
			Thread.sleep(fiveSeconds); // đợi 5 giây
			try {
				WebElement avatarBtn = driver.findElement(By.xpath("//button[@id='avatar-btn']"));
				avatarBtn.click();
			} catch (Exception e) {
				String st = email + " " + password;
				data.add(st);
				System.out.println("Error: " + e.getMessage() + ". " + e.getCause());
				continue;
			}
			Thread.sleep(fiveSeconds); // đợi 5 giây
			try {
				WebElement subtitle = driver.findElement(By.xpath(
						"//yt-formatted-string[contains(text(), 'Tạo kênh') or contains(text(), 'Create a channel')][@id='label']"));
				subtitle.click();
				Thread.sleep(tenSeconds); // đợi 10 giây
				WebElement createChannel = driver
						.findElement(By.xpath("//button[@aria-label='Tạo kênh' or @aria-label='Create channel']"));
				createChannel.click();
				Thread.sleep(tenSeconds); // đợi 10 giây
				WebElement avatarB = driver.findElement(By.xpath("//button[@id='avatar-btn']"));
				avatarB.click();
				Thread.sleep(fiveSeconds); // đợi 5 giây
				List<WebElement> elements = driver.findElements(By.xpath(
						"//yt-formatted-string[contains(text(), 'Đăng xuất') or contains(text(), 'Sign out')][@id='label']"));
				WebElement signout = elements.get(0);
				JavascriptExecutor executor = (JavascriptExecutor) driver;
				executor.executeScript("arguments[0].click();", signout);
			} catch (Exception e) {
				try {
					System.out.println("Error: " + e.getMessage() + ". " + e.getCause());
					List<WebElement> elements = driver.findElements(By.xpath(
							"//yt-formatted-string[contains(text(), 'Đăng xuất') or contains(text(), 'Sign out')][@id='label']"));
					WebElement signout = elements.get(0);
					JavascriptExecutor executor = (JavascriptExecutor) driver;
					executor.executeScript("arguments[0].click();", signout);
				} catch (Exception e2) {
					String st = email + " " + password;
					data.add(st);
					System.out.println("Error: " + e.getMessage() + ". " + e.getCause());
					continue;
				}
			}
			System.out.println("Successfully created channel with Email: " + email);
		}
		br.close();

		Set<String> setWithoutDuplicates = new HashSet<>(data);
		List<String> masterData = new ArrayList<>(setWithoutDuplicates);

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
			for (String entry : masterData) {
				bw.write(entry);
				bw.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Đóng trình duyệt
		driver.quit();
	}

}
