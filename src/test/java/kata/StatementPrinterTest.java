package kata;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class StatementPrinterTest {

    private static final String CUSTOMER = "BigCo";
    private static final Performance PERFORMANCE_TRAGEDY_1 = new Performance("hamlet", 10);
    private static final Performance PERFORMANCE_COMEDY = new Performance("as-like", 15);
    private static final Performance PERFORMANCE_TRAGEDY_2 = new Performance("othello", 20);
    private static final Performance PERFORMANCE_HISTORY = new Performance("caesar", 23);
    private static final String PLAY_TRAGEDY_KEY = "hamlet";
    private static final Play PLAY_TRAGEDY = new Play("Hamlet", "tragedy");
    private static final String PLAY_COMEDY_KEY = "as-like";
    private static final Play PLAY_COMEDY = new Play("As You Like It", "comedy");
    private static final String PLAY_TRAGEDY_2_KEY = "othello";
    private static final Play PLAY_TRAGEDY_2 = new Play("Othello", "tragedy");
    private static final String PLAY_HISTORY_KEY = "caesar";
    private static final Play PLAY_HISTORY = new Play("Caesar", "history");
    private StatementPrinter printer;

    @BeforeEach
    void setup() {
        printer = new StatementPrinter();
    }

    @Test
    void print_simple_invoice_no_plays() {
        List<Performance> performances = List.of();
        Map<String, Play> plays = Map.of();
        Invoice bigCo = new Invoice(CUSTOMER, performances);

        final String receipt = printer.print(bigCo, plays);

        assertThat(receipt).isEqualTo("""
                                                Statement for BigCo
                                                Amount owed is $0.00
                                                You earned 0 credits
                                                """);
    }

    @Test
    void print_simple_invoice_no_plays_html() {
        List<Performance> performances = List.of();
        Map<String, Play> plays = Map.of();
        Invoice bigCo = new Invoice(CUSTOMER, performances);

        final String receipt = printer.print(bigCo, plays, true);

        assertThat(receipt).isEqualTo("""
                                                <h1>Statement for BigCo</h1>
                                                <p>
                                                Amount owed is <em>$0.00</em>
                                                </p>
                                                <p>
                                                You earned <em>0</em> credits
                                                </p>
                                                """);
    }

    @Test
    void print_simple_invoice_one_play() {
        List<Performance> performances = List.of(PERFORMANCE_TRAGEDY_1);
        Map<String, Play> plays = Map.of(PLAY_TRAGEDY_KEY, PLAY_TRAGEDY);
        Invoice bigCo = new Invoice(CUSTOMER, performances);

        final String receipt = printer.print(bigCo, plays);

        assertThat(receipt).isEqualTo("""
                                              Statement for BigCo
                                                Hamlet: $400.00 (10 seats)
                                              Amount owed is $400.00
                                              You earned 0 credits
                                                """);
    }

    @Test
    void print_simple_invoice_one_play_html() {
        List<Performance> performances = List.of(PERFORMANCE_TRAGEDY_1);
        Map<String, Play> plays = Map.of(PLAY_TRAGEDY_KEY, PLAY_TRAGEDY);
        Invoice bigCo = new Invoice(CUSTOMER, performances);

        final String receipt = printer.print(bigCo, plays, true);

        assertThat(receipt).isEqualTo("""
                                              <h1>Statement for BigCo</h1>
                                              <table>
                                              <tr>
                                                <th>play</th>
                                                <th>seats</th>
                                                <th>cost</th>
                                              </tr>
                                              <tr>
                                                <td>Hamlet</td>
                                                <td>$400.00</td>
                                                <td>10</td>
                                              </tr>
                                              </table>
                                              <p>
                                              Amount owed is <em>$400.00</em>
                                              </p>
                                              <p>
                                              You earned <em>0</em> credits
                                              </p>
                                                """);
    }
    @Test
    void print_simple_invoice_one_play_performance_with_history_type() {
        List<Performance> performances = List.of(PERFORMANCE_HISTORY);
        Map<String, Play> plays = Map.of(PLAY_HISTORY_KEY, PLAY_HISTORY);
        Invoice bigCo = new Invoice(CUSTOMER, performances);

        assertThatExceptionOfType(Error.class).isThrownBy(() -> printer.print(bigCo, plays));
    }

    @Test
    void print_simple_invoice_two_plays() {
        List<Performance> performances = List.of(PERFORMANCE_TRAGEDY_1, PERFORMANCE_COMEDY);
        Map<String, Play> plays = Map.of(PLAY_TRAGEDY_KEY, PLAY_TRAGEDY, PLAY_COMEDY_KEY, PLAY_COMEDY);
        Invoice bigCo = new Invoice(CUSTOMER, performances);

        final String receipt = printer.print(bigCo, plays);

        assertThat(receipt).isEqualTo("""
                                              Statement for BigCo
                                                Hamlet: $400.00 (10 seats)
                                                As You Like It: $345.00 (15 seats)
                                              Amount owed is $745.00
                                              You earned 3 credits
                                                """);
    }

    @Test
    void print_simple_invoice_two_plays_html() {
        List<Performance> performances = List.of(PERFORMANCE_TRAGEDY_1, PERFORMANCE_COMEDY);
        Map<String, Play> plays = Map.of(PLAY_TRAGEDY_KEY, PLAY_TRAGEDY, PLAY_COMEDY_KEY, PLAY_COMEDY);
        Invoice bigCo = new Invoice(CUSTOMER, performances);

        final String receipt = printer.print(bigCo, plays, true);

        assertThat(receipt).isEqualTo("""
                                              <h1>Statement for BigCo</h1>
                                              <table>
                                              <tr>
                                                <th>play</th>
                                                <th>seats</th>
                                                <th>cost</th>
                                              </tr>
                                              <tr>
                                                <td>Hamlet</td>
                                                <td>$400.00</td>
                                                <td>10</td>
                                              </tr>
                                              <tr>
                                                <td>As You Like It</td>
                                                <td>$345.00</td>
                                                <td>15</td>
                                              </tr>
                                              </table>
                                              <p>
                                              Amount owed is <em>$745.00</em>
                                              </p>
                                              <p>
                                              You earned <em>3</em> credits
                                              </p>
                                                """);
    }

    @Test
    void print_simple_invoice_three_plays() {
        List<Performance> performances = List.of(PERFORMANCE_TRAGEDY_1, PERFORMANCE_COMEDY, PERFORMANCE_TRAGEDY_2);
        Map<String, Play> plays = Map.of(PLAY_TRAGEDY_KEY, PLAY_TRAGEDY, PLAY_COMEDY_KEY, PLAY_COMEDY, PLAY_TRAGEDY_2_KEY,
                                         PLAY_TRAGEDY_2);
        Invoice bigCo = new Invoice(CUSTOMER, performances);

        final String receipt = printer.print(bigCo, plays);

        assertThat(receipt).isEqualTo("""
                                              Statement for BigCo
                                                Hamlet: $400.00 (10 seats)
                                                As You Like It: $345.00 (15 seats)
                                                Othello: $400.00 (20 seats)
                                              Amount owed is $1,145.00
                                              You earned 3 credits
                                                """);
    }

    @Test
    void print_simple_invoice_three_plays_html() {
        List<Performance> performances = List.of(PERFORMANCE_TRAGEDY_1, PERFORMANCE_COMEDY, PERFORMANCE_TRAGEDY_2);
        Map<String, Play> plays = Map.of(PLAY_TRAGEDY_KEY, PLAY_TRAGEDY, PLAY_COMEDY_KEY, PLAY_COMEDY, PLAY_TRAGEDY_2_KEY,
                                         PLAY_TRAGEDY_2);
        Invoice bigCo = new Invoice(CUSTOMER, performances);

        final String receipt = printer.print(bigCo, plays, true);

        assertThat(receipt).isEqualTo("""
                                              <h1>Statement for BigCo</h1>
                                              <table>
                                              <tr>
                                                <th>play</th>
                                                <th>seats</th>
                                                <th>cost</th>
                                              </tr>
                                              <tr>
                                                <td>Hamlet</td>
                                                <td>$400.00</td>
                                                <td>10</td>
                                              </tr>
                                              <tr>
                                                <td>As You Like It</td>
                                                <td>$345.00</td>
                                                <td>15</td>
                                              </tr>
                                              <tr>
                                                <td>Othello</td>
                                                <td>$400.00</td>
                                                <td>20</td>
                                              </tr>
                                              </table>
                                              <p>
                                              Amount owed is <em>$1,145.00</em>
                                              </p>
                                              <p>
                                              You earned <em>3</em> credits
                                              </p>
                                                """);
    }
}