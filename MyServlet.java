package mypackage;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Scanner;

import org.apache.jasper.tagplugins.jstl.core.Url;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class MyServlet
 */
public class MyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MyServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String dataInput=request.getParameter("city");
//		System.out.print(dataInput);
		String apiKey="d9e979e6818ab84f6fe88e32e136df87";
		//get the city from the user input 
		String city=request.getParameter("city");
		
		 // Create the URL for the OpenWeatherMap API request
		  String apiUrl=  "https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid="+apiKey;

       // String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey;
//		doGet(request, response);
        //Api Integration
		  try {
        URL url= new URL(apiUrl);
        HttpURLConnection connection= (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        // reading the data from network 
        InputStream inputstream= connection.getInputStream();
        InputStreamReader reader= new InputStreamReader(inputstream);
        //Store data 
      
        StringBuilder responseContent= new StringBuilder();
        // input lene ke liye from the reader ,user crete scanner object 
        Scanner scanner=new Scanner(reader);
        while(scanner.hasNext()) {
        	responseContent.append(scanner.nextLine());
        }
        scanner.close();
//        System.out.println(responseContent);
//        System.out.println();
        //type casting =parsing data string to json
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(responseContent.toString(), JsonObject.class);
//        System.out.println( jsonObject );
        //Date & Time
        long dateTimestamp = jsonObject.get("dt").getAsLong() * 1000;
        String date = new Date(dateTimestamp).toString();
        
        //Temperature
        double temperatureKelvin = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
        int temperatureCelsius = (int) (temperatureKelvin - 273.15);
       
        //Humidity
        int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();
        
        //Wind Speed
        double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();
        
        //Weather Condition
        String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();
     // Set the data as request attributes (for sending to the jsp page)
        request.setAttribute("date", date);
        request.setAttribute("city", city);
        request.setAttribute("temperature", temperatureCelsius);
        request.setAttribute("weatherCondition", weatherCondition); 
        request.setAttribute("humidity", humidity);    
        request.setAttribute("windSpeed", windSpeed);
        request.setAttribute("weatherData", responseContent.toString());
        connection.disconnect();
       
	}catch (IOException e) {
        e.printStackTrace();
    }
	 //forword to the request to weather.jsp page for rendring 
    request.getRequestDispatcher("weather.jsp").forward(request, response);

}
}

