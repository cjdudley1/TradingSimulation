
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author James G
 */
public class ViewController extends Application {

    private BorderPane root = new BorderPane();
    private SettingsViewer simSettings;
    private ChartViewer stocksChart;
    private FilterTreeViewer filterTree;
    private EventViewer eventsLog;

    private StockExchange exchange;
    
    private Timeline ticker;
    public boolean atStart = true;
    public boolean completed = false;

    @Override
    public void start(Stage primaryStage) {
        root.setRight(filterTree.getFxNode());
        root.setLeft(stocksChart.getFxNode());
        root.setBottom(new HBox(eventsLog.getFxNode(), simSettings.getFxNode()));

        Scene scene = new Scene(root, 880, 550);
        primaryStage.setTitle("Stock Market Simulation");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(e -> {
            System.out.println("Stage is closing");
            ticker.stop();
        });
    }

    /**
     * Constructs the UI classes and passes with required variables
     */
    public ViewController(StockExchange exchange) {
        this.exchange = exchange;
        System.out.println(":: Launched Program");
        //exchange = (StockExchange) TradingSimulation.getTradeExchange().getMarket(0);
        simSettings = new SettingsViewer(this);
        stocksChart = new ChartViewer(this, null);
        //stocksChart = new ChartViewer(this, exchange.getCompanies());
        filterTree = new FilterTreeViewer(this);
        eventsLog = new EventViewer(this, null);
        //eventsLog = new EventViewer(this, exchange.getEvents());
                
        ticker = new Timeline(new KeyFrame(Duration.minutes(15), e -> {
            System.out.println("::VWC:: Stepping Tick");
            if (exchange.getCurrTick() <= exchange.getEndTick()) {
                this.update();
            } else {
                completed = true;
                ticker.stop();
            }
        }));
        ticker.setCycleCount(Animation.INDEFINITE);     
        System.out.println(":: Ticker Object Activated");
    }

    /**
     * Start up the UI
     */
    public static void readyGUI() {
        launch();
    }

    /**
     * Run the simulation
     */
    public void playSimulation() {
        System.out.println("::CMD:: Play Simulation");
        atStart = false;
        ticker.play();
    }

    /**
     * Pause the simulation
     */
    public void pauseSimulation() {
        System.out.println("::CMD:: Pause Simulation");
        ticker.pause();
    }

    /**
     * Set the speed multiplier of the simulation
     *
     * @param newSpeed The new speed
     */
    public void setSpeed(int newSpeed) {
        System.out.println("::CMD:: Change Speed");
        ticker.setRate(newSpeed);
    }

    /**
     * Update the simulation View and Model
     */
    public void update() {
        System.out.println("::PRC:: Step Tick");
        exchange.tick();
        System.out.println(exchange);
        stocksChart.updateAllSeries();
        eventsLog.displayEventsForTick(exchange.getCurrTick());
    }

    /**
     * Stop the simulation and reset to the start
     */
    public void reset() {
        atStart = true;
        completed = false;
        
        ticker.stop();
        eventsLog.clearEventsLog();
        stocksChart.clearStocksChart();

        //exchange.reset()
    }

    /**
     * Get the chart UI element
     *
     * @return ChartViewer Chart class
     */
    public ChartViewer getChart() {
        return stocksChart;
    }

    /**
     * Get the event UI element
     *
     * @return EventViewer Event class
     */
    public EventViewer getEventLog() {
        return eventsLog;
    }

    /**
     * Get the tree UI element
     *
     * @return FilterTreeViewer Tree class
     */
    public FilterTreeViewer getFilterTree() {
        return filterTree;
    }

    /**
     * Get the settings UI element
     *
     * @return SettingsViewer Settings class
     */
    public SettingsViewer getSettings() {
        return simSettings;
    }

    /**
     * Get the stock exchange class
     *
     * @return StockExchange
     */
    public StockExchange getExchange() {
        return exchange;
    }

    
    public void reportBadFile(String stock_Init_Data_was_not_valid_for_file_re) {
        PopupWindow.display("Message", 200);
    }

}
