package de.uniwue.jpp.mensabot.gui;

import de.uniwue.jpp.errorhandling.OptionalWithMessage;
import de.uniwue.jpp.errorhandling.OptionalWithMessageVal;
import de.uniwue.jpp.mensabot.dataclasses.Meal;
import de.uniwue.jpp.mensabot.dataclasses.Menu;
import de.uniwue.jpp.mensabot.retrieval.Fetcher;
import de.uniwue.jpp.mensabot.retrieval.Parser;
import de.uniwue.jpp.mensabot.retrieval.Saver;
import de.uniwue.jpp.mensabot.sending.Sender;
import de.uniwue.jpp.mensabot.sending.formatting.Formatter;
import de.uniwue.jpp.mensabot.sending.formatting.analyse.Analyzer;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

public class mainController implements Initializable {
    @FXML
    private ListView<String> listMethodeFormatter;

    @FXML
    private Label menuLabel;

    @FXML
    private Label menuItems;

    @FXML
    private Label analysisLabel;

    @FXML
    private Label analysisResult;

    @FXML
    private ListView<String> listMethodeFormatterCantSelect;

    @FXML
    private ListView<String> listOfAnalyzersAvailable;

    @FXML
    private ListView<String> listOfSelectedAnalyzers;

    @FXML
    private ListView<String> listOfAnalyzers;

    @FXML
    private TextArea formatterFromFormat;

    @FXML
    private TextField nameFormatterFromFormat;

    @FXML
    private Label errLabel;

    @FXML
    private TextField headLineFormatter;

    @FXML
    private TextField nameHeadLineFormatter;

    @FXML
    private CategoryAxis xLineChart;

    @FXML
    private NumberAxis yLineChart;

    @FXML
    private LineChart<?, ?> lineChart;



    @FXML
    private TextField searchKey;

    @FXML
    private PieChart pieChart;

    @FXML
    private BarChart<?,?> barChart;

    @FXML
    private TableView<Stats> tableStats;

    @FXML
    private TableColumn<Stats,String> nameStats;

    @FXML
    private TableColumn<Stats,String> singleMenuStats;

    @FXML
    private TableColumn<Stats,String> allMenuStats;

    @FXML
    private TableView<MealOnTable> tableLogViewer;

    @FXML
    private TableColumn<MealOnTable, String> date;

    @FXML
    private Button nextRow;

    @FXML
    private Label resultSearch;

    private Set<Meal> meals;

    private String dataFetched;

    private Menu menu;

    private Map<String, Analyzer<?>> analyzerMap;

    private Map<String, Formatter> formatterMap;


    ObservableList<String> formatters = FXCollections.observableArrayList(
            "SimpleFormatter",
            "HiddenFormatter",
            "FirstWordFormatter",
            "ShortFormatter",
            "AlphabeticalOrderFormatter",
            "PricelessFormatter"
    );

    ObservableList<String> analyzers = FXCollections.observableArrayList(
            "AveragePrizeAnalyzer",
            "MedianPrizeAnalyzer",
            "MinPrizeMealAnalyzer",
            "MaxPrizeMealAnalyzer",
            "TotalPrizeAnalyzer"
    );
    ObservableList<String> selectedAnalyzers = FXCollections.observableArrayList(

    );

    ObservableList<Stats> listOfStats = FXCollections.observableArrayList(

    );
    ObservableList<MealOnTable> listOfDate = FXCollections.observableArrayList(

    );


    public void formatData(){
        String formatSelected = listMethodeFormatter.getSelectionModel().getSelectedItem();
        if(meals != null) {
            if (formatSelected != null) {
                LocalDate date = LocalDate.now();
                menuLabel.setText("Essen am : " + date.toString());

                List<Menu> allMenus = new ArrayList<>();
                allMenus.add(menu);
                dataFetched = formatterMap.get(formatSelected).format(menu,() -> OptionalWithMessage.of(allMenus)).get();
                menuItems.setText(dataFetched);
                int avg = Analyzer.createAveragePrizeAnalyzer().analyze(allMenus).get();
                int median = Analyzer.createMedianPrizeAnalyzer().analyze(allMenus).get();
                String cheapestMeal = Analyzer.createMinPrizeMealAnalyzer().analyze(allMenus).get().toString();
                String expensiveMeal = Analyzer.createMaxPrizeMealAnalyzer().analyze(allMenus).get().toString();
                int totalPrice = Analyzer.createTotalPrizeAnalyzer().analyze(allMenus).get();

                analysisLabel.setText("Statistiken fuer " + date + ":");
                String analysisRes = "";
                analysisRes += "Average Prize:\t\t\t\t\t" + avg + System.lineSeparator();
                analysisRes += "Median:\t\t\t\t\t\t" + median + System.lineSeparator();
                analysisRes += "Cheapest Meal Prize:\t\t\t" + cheapestMeal + System.lineSeparator();;
                analysisRes += "Expensive Meal Prize:\t\t\t" + expensiveMeal + System.lineSeparator();;
                analysisRes += "Total Prize:\t\t\t\t\t" + totalPrice + System.lineSeparator();;

                analysisResult.setText(analysisRes);

            } else
                menuLabel.setText("please select a format methode to formatter!");
        }else {
            menuLabel.setText("please fetch data first (click on Fetch & Save button).");
        }

    }
    public void fetchData() {

        OptionalWithMessage<String> dataAsCsv = Fetcher.createDummyCsvFetcher().fetchCurrentData();
        if (dataAsCsv instanceof OptionalWithMessageVal) {
            OptionalWithMessage<Menu> dataAsMenu = Parser.createCsvParser().parse(dataAsCsv.get());
            menu = dataAsMenu.get();
            meals = dataAsMenu.get().getMeals();
            if (dataAsMenu instanceof OptionalWithMessageVal) {
                Optional<String> saver = Saver.createCsvSaver().log(Path.of(System.getProperty("user.dir")+"\\src\\de\\uniwue\\jpp\\mensabot\\retrieval\\log.csv"), dataAsMenu.get());
                if (saver.equals(Optional.empty())) {
                    menuLabel.setText("done successfully!");
                    dataFetched = dataAsCsv.get();
                }
                else
                    menuLabel.setText("error during save data: " + saver.get());
            }else {
                menuLabel.setText("error during parse data : " + dataAsMenu.getMessage());
            }
        }else {
            menuLabel.setText("error during fetch data: " + dataAsCsv.getMessage());
        }
    }

    public void sendData(){
        Sender.createDummySender();
    }

    public void selectAnalyzer(){
        String analyzer = listOfAnalyzersAvailable.getSelectionModel().getSelectedItem();
        selectedAnalyzers.add(analyzer);
        listOfSelectedAnalyzers.setItems(selectedAnalyzers);
    }
    public void unselectAnalyzer(){
        int unselectAnalyzerIndex = listOfSelectedAnalyzers.getSelectionModel().getSelectedIndices().get(0);
        selectedAnalyzers.remove(unselectAnalyzerIndex);
        listOfSelectedAnalyzers.setItems(selectedAnalyzers);
    }

    public void createNewFormatter(){
        String formatString = formatterFromFormat.getText();
        List<Analyzer<?>> listOfAnalyzersSelected = new ArrayList<>();
        String name = nameFormatterFromFormat.getText();
        for(int i = 0 ; i < selectedAnalyzers.size() ; i++)
        {
            listOfAnalyzersSelected.add( analyzerMap.get(selectedAnalyzers.get(i)));
        }

        try {
            Formatter formatter = Formatter.createFormatterFromFormat(formatString,listOfAnalyzersSelected,name);
            if (!formatterMap.containsKey(name))
                formatters.add(formatter.toString());
            formatterMap.put(name,formatter);

            listMethodeFormatter.setItems(formatters);
            listMethodeFormatterCantSelect.setItems(formatters);
            errLabel.setText("done!");

        }catch (IllegalArgumentException e)
        {
            errLabel.setText(e.getMessage());
        }
    }

    public void createNewHeadlineFormatter(){
        String headLine = headLineFormatter.getText();
        String name = nameHeadLineFormatter.getText();
        String selectedItem = listOfAnalyzers.getSelectionModel().getSelectedItem();
        Analyzer<?> analyzer = analyzerMap.get(selectedItem);
        List<Menu> allMenus = new ArrayList<>();
        allMenus.add(menu);
        try {
            if (name.isEmpty() && !formatterMap.containsKey("FormatterFromAnalyzer"))
                formatters.add("FormatterFromAnalyzer");

            if (!name.isEmpty() && !formatterMap.containsKey(name))
                formatters.add(name);

            if(name.isEmpty())
                formatterMap.put("FormatterFromAnalyzer",Formatter.createFormatterFromAnalyzer(headLine,analyzer));
            else
                formatterMap.put(name,Formatter.createFormatterFromAnalyzer(headLine,analyzer,name));

            listMethodeFormatter.setItems(formatters);
            listMethodeFormatterCantSelect.setItems(formatters);
            errLabel.setText("done!");

        }catch (IllegalArgumentException e)
        {
            errLabel.setText(e.getMessage());
        }
    }

    public void createLineChart(){
        try {
            XYChart.Series series1 = new XYChart.Series();
            XYChart.Series series2 = new XYChart.Series();
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream is = classloader.getResourceAsStream("mensalog.csv");
            InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(streamReader);
            List<Menu> data = new ArrayList<>();
            for (String line; (line = reader.readLine()) != null; ) {
           //     System.out.println(line);
                OptionalWithMessage<Menu> menu = Parser.createCsvParser().parse(line);
                data.add(menu.get());


            }
            Map<LocalDate,Double> avgPrize = Analyzer.createAveragePrizePerDayAnalyzer().analyze(data).get();
            Map<LocalDate,Double> totalPrize = Analyzer.createTotalPrizePerDayAnalyzer().analyze(data).get();
            for (Map.Entry<LocalDate,Double> day : avgPrize.entrySet()){
                series1.getData().add(new XYChart.Data(day.getKey().toString(),day.getValue()));
            }

            for (Map.Entry<LocalDate,Double> day : totalPrize.entrySet()){
                series2.getData().add(new XYChart.Data(day.getKey().toString(),day.getValue()));
            }

            lineChart.getData().addAll(series1,series2);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createPicChart(){

        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream is = classloader.getResourceAsStream("mensalog.csv");
            InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(streamReader);
            List<Menu> data = new ArrayList<>();
            for (String line; (line = reader.readLine()) != null; ) {
                //     System.out.println(line);
                OptionalWithMessage<Menu> menu = Parser.createCsvParser().parse(line);
                data.add(menu.get());
            }
            Map<Integer,Long> range = Analyzer.createPrizeRangeAnalyzer().analyze(data).get();
            ObservableList<PieChart.Data> pieChartData
                    = FXCollections.observableArrayList();
            int maxPrice = (int)(Math.ceil(Analyzer.createMaxPrizeMealAnalyzer().analyze(data).get().getPrice() / 100.0));
            System.out.println(maxPrice);
            for (int i = 0 ; i <= maxPrice ; i++){
                if (range.containsKey(i))
                    pieChartData.add(new PieChart.Data(i+"-"+(i+1), range.get(i)));
            }

            pieChart.setData(pieChartData);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createBarChart(){

        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream is = classloader.getResourceAsStream("mensalog.csv");
            InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(streamReader);
            List<Menu> data = new ArrayList<>();
            for (String line; (line = reader.readLine()) != null; ) {
                OptionalWithMessage<Menu> menu = Parser.createCsvParser().parse(line);
                data.add(menu.get());
            }
            Map<LocalDate,Integer> amountOfDishes = Analyzer.createAmountOfDishesPerDayAnalyzer().analyze(data).get();
            XYChart.Series series = new XYChart.Series();

            for (Map.Entry<LocalDate,Integer> amount : amountOfDishes.entrySet()){
                series.getData().add(new XYChart.Data(amount.getKey().toString(),amount.getValue()));
            }

            barChart.getData().addAll(series);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void createTableStats(){
        nameStats.setCellValueFactory(new PropertyValueFactory<Stats,String>("nameStats"));
        singleMenuStats.setCellValueFactory(new PropertyValueFactory<Stats,String>("singleMenuStats"));
        allMenuStats.setCellValueFactory(new PropertyValueFactory<Stats,String>("allMenuStats"));
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream is = classloader.getResourceAsStream("mensalog.csv");
            InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(streamReader);
            List<Menu> data = new ArrayList<>();
            List<Menu> dataToThisDay = new ArrayList<>();
            dataToThisDay.add(Parser.createCsvParser().parse(Fetcher.createDummyCsvFetcher().fetchCurrentData().get()).get());

            for (String line; (line = reader.readLine()) != null; ) {
                OptionalWithMessage<Menu> menu = Parser.createCsvParser().parse(line);
                data.add(menu.get());
            }
            String  all = getPriceAsString(Analyzer.createMaxPrizeMealAnalyzer().analyze(data).get().getPrice());
            String  single = getPriceAsString(Analyzer.createMaxPrizeMealAnalyzer().analyze(dataToThisDay).get().getPrice());
            String name = "MaxPrizeMeal";
            listOfStats.add(new Stats(name,single,all));

            all = getPriceAsString(Analyzer.createMinPrizeMealAnalyzer().analyze(data).get().getPrice());
            single = getPriceAsString(Analyzer.createMinPrizeMealAnalyzer().analyze(dataToThisDay).get().getPrice());
            name = "MinPrizeMeal";
            listOfStats.add(new Stats(name,single,all));

            all = getPriceAsString(Analyzer.createMedianPrizeAnalyzer().analyze(data).get());
            single = getPriceAsString(Analyzer.createMedianPrizeAnalyzer().analyze(dataToThisDay).get());
            name = "MedianPrizeMeal";
            listOfStats.add(new Stats(name,single,all));

            all = getPriceAsString(Analyzer.createAveragePrizeAnalyzer().analyze(data).get());
            single = getPriceAsString(Analyzer.createAveragePrizeAnalyzer().analyze(dataToThisDay).get());
            name = "AveragePrizeMeal";
            listOfStats.add(new Stats(name,single,all));

            Map<LocalDate,Integer> mapAll = Analyzer.createAmountOfDishesPerDayAnalyzer().analyze(data).get();
            Map<LocalDate,Integer> mapSingle = Analyzer.createAmountOfDishesPerDayAnalyzer().analyze(dataToThisDay).get();
            name = "AmountOfDishesPerDay";
            single = "";
            all ="";
            for (Map.Entry<LocalDate,Integer> day : mapAll.entrySet()){
               all += day.getKey().toString()+" : "+day.getValue() + System.lineSeparator();
            }
            for (Map.Entry<LocalDate,Integer> day : mapSingle.entrySet()){
                single += day.getKey().toString()+" : "+day.getValue() + System.lineSeparator();
            }
            listOfStats.add(new Stats(name,single,all));

            Map<Meal,Long> mapAllMeals = Analyzer.createRepetitionAnalyzer().analyze(data).get();
            Map<Meal,Long> mapSingleMeals = Analyzer.createRepetitionAnalyzer().analyze(dataToThisDay).get();
            name = "RepetitionMeals";
            single = "";
            all ="";
            for (Map.Entry<Meal,Long> day : mapAllMeals.entrySet()){
                all += day.getKey().getName()+" : "+day.getValue() + System.lineSeparator();
            }
            for (Map.Entry<Meal,Long> day : mapSingleMeals.entrySet()){
                single += day.getKey().getName()+" : "+day.getValue() + System.lineSeparator();
            }
            listOfStats.add(new Stats(name,single,all));

            Map<Integer,Long> mapAllMealsRange = Analyzer.createPrizeRangeAnalyzer().analyze(data).get();
            Map<Integer,Long> mapSingleMealsRange = Analyzer.createPrizeRangeAnalyzer().analyze(dataToThisDay).get();
            name = "PrizeRange";
            single = "";
            all ="";
            for (Map.Entry<Integer,Long> day : mapAllMealsRange.entrySet()){
                all += day.getKey()+" : "+day.getValue() + System.lineSeparator();
            }
            for (Map.Entry<Integer,Long> day : mapSingleMealsRange.entrySet()){
                single += day.getKey() +" : "+day.getValue() + System.lineSeparator();
            }
            listOfStats.add(new Stats(name,single,all));

            tableStats.setPrefWidth(1150);
            tableStats.setItems(listOfStats);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String getPriceAsString(int price){
        int x = price / 100;
        int y = price % 100 /  10;
        int z = price % 10;
        return x + ","+ y + z +"\u20ac";
    }

    List<MealOnTable> findIn = new ArrayList<>();
    List<Integer> rowNumber = new ArrayList<>();
    List<TableColumn<MealOnTable,String>> colNumber = new ArrayList<>();
    int idxFind = 0;

    public void search(){
        String text = searchKey.getText();
        text = text.toLowerCase();
        ObservableList<TableColumn<MealOnTable,?> > columns = tableLogViewer.getColumns();
        findIn = new ArrayList<>();
        for (int rowIdx = 0 ; rowIdx < tableLogViewer.getItems().size() ; rowIdx++){
            MealOnTable row = tableLogViewer.getItems().get(rowIdx);
            for(TableColumn column : columns){
               String content = column.getCellObservableValue(row).getValue().toString().toLowerCase();
               if (content.contains(text)) {
                    findIn.add(row);
                    rowNumber.add(rowIdx);
                    colNumber.add(column);
               }
            }
        }
        if (!findIn.isEmpty())
        {
            nextRow.setVisible(true);
            resultSearch.setVisible(true);
            next();
        }
        else {
            nextRow.setVisible(false);
            resultSearch.setVisible(false);
        }

    }
    public void next(){
        int i = (idxFind++) % findIn.size();
        resultSearch.setText("result " + (i + 1) + "/"+findIn.size()+ " in row: "+(rowNumber.get(i) + 1));
        tableLogViewer.requestFocus();
        tableLogViewer.getSelectionModel().select(findIn.get(i));
        tableLogViewer.getFocusModel().focus(rowNumber.get(i));

    }
    public void createTableLogViewer(){

        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream is = classloader.getResourceAsStream("mensalog.csv");
            InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(streamReader);
            List<Menu> data = new ArrayList<>();
            for (String line; (line = reader.readLine()) != null; ) {
                OptionalWithMessage<Menu> menu = Parser.createCsvParser().parse(line);
                data.add(menu.get());
            }

            int maxNumberOfMeals = 0;
            Map<LocalDate,Integer> localDateIntegerMap = Analyzer.createAmountOfDishesPerDayAnalyzer().analyze(data).get();
            for (Map.Entry<LocalDate,Integer> localDateIntegerEntry : localDateIntegerMap.entrySet()){
                maxNumberOfMeals = Math.max(maxNumberOfMeals,localDateIntegerEntry.getValue());
            }
            TableColumn<MealOnTable ,String>[] mealsCols = new TableColumn[maxNumberOfMeals];
            TableColumn<MealOnTable ,String>[] pricesCols = new TableColumn[maxNumberOfMeals];

            date.setCellValueFactory(new PropertyValueFactory<MealOnTable,String>("date"));
            date.setPrefWidth(100);
           for(int i = 0,j = 0 ; i < maxNumberOfMeals ; i++ ,j++)
            {
                List<String> prices = new ArrayList<>();
                List<String> meals = new ArrayList<>();

                mealsCols[i] = new TableColumn<>("meal " + (j+1));
                int finalI = i;
                mealsCols[i].setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MealOnTable, String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<MealOnTable, String> param) {
                        ObservableValue<String> res = new ObservableValueBase<String>() {
                            @Override
                            public String getValue() {
                                return param.getValue().getMeals().get(finalI);
                            }
                        };
                        return res;
                    }
                });
                mealsCols[i].setPrefWidth(450);
                pricesCols[i] = new TableColumn<>("price " + (j+1));
                pricesCols[i].setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MealOnTable, String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<MealOnTable, String> param) {
                        ObservableValue<String> res = new ObservableValueBase<String>() {
                            @Override
                            public String getValue() {
                                return param.getValue().getPrices().get(finalI);
                            }
                        };
                        return res;
                    }
                });
                pricesCols[i].setPrefWidth(50);
                tableLogViewer.getColumns().add(mealsCols[i] );
                tableLogViewer.getColumns().add(pricesCols[i]);
            }


            tableLogViewer.setPrefWidth(maxNumberOfMeals  * 500 + 150);
            for (Menu menu : data){
                String date = menu.getDate().toString();
                Set<Meal> mealsSet = menu.getMeals();
                ObservableList<String> mealsList = FXCollections.observableArrayList();
                ObservableList<String> pricesList = FXCollections.observableArrayList();
                for (Meal meal : mealsSet){
                    mealsList.add(meal.getName());
                    pricesList.add(getPriceAsString(meal.getPrice()));
                }
                for(int i = mealsSet.size() ; i < maxNumberOfMeals ; i++){
                    mealsList.add("");
                    pricesList.add("");
                }
                listOfDate.add(new MealOnTable(date,mealsList,pricesList));
            }

            tableLogViewer.setItems(listOfDate);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    public void updateLogViewerTable(){
        listOfDate.remove(0,listOfDate.size());
        createTableLogViewer();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
            formatterMap = new HashMap<>();
            formatterMap.put("SimpleFormatter",Formatter.createSimpleFormatter());
            formatterMap.put("HiddenFormatter",Formatter.createHiddenFormatter());
            formatterMap.put("FirstWordFormatter",Formatter.createFirstWordFormatter());
            formatterMap.put("ShortFormatter",Formatter.createShortFormatter());
            formatterMap.put("AlphabeticalOrderFormatter",Formatter.createAlphabeticalOrderFormatter());
            formatterMap.put("PricelessFormatter",Formatter.createPricelessFormatter());

            analyzerMap = new HashMap<>();
            analyzerMap.put("AveragePrizeAnalyzer", Analyzer.createAveragePrizeAnalyzer());
            analyzerMap.put("MedianPrizeAnalyzer", Analyzer.createMedianPrizeAnalyzer());
            analyzerMap.put("MinPrizeMealAnalyzer", Analyzer.createMinPrizeMealAnalyzer());
            analyzerMap.put("MaxPrizeMealAnalyzer", Analyzer.createMaxPrizeMealAnalyzer());
            analyzerMap.put("TotalPrizeAnalyzer", Analyzer.createTotalPrizeAnalyzer());

            listMethodeFormatter.setItems(formatters);
            listMethodeFormatterCantSelect.setItems(formatters);
            listOfAnalyzersAvailable.setItems(analyzers);
            listOfAnalyzers.setItems(analyzers);
            createLineChart();
            createPicChart();
            createBarChart();
            createTableStats();
            createTableLogViewer();





    }
}
