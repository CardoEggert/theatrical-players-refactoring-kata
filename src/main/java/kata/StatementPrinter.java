package kata;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StatementPrinter {

    public String print(Invoice invoice, Map<String, Play> plays) {
        return print(invoice, plays, false);
    }

    public String print(Invoice invoice, Map<String, Play> plays, boolean convertToHtml) {
        final List<TableRow> rowsToPrint = calculateAndReturnTableRows(invoice, plays);
        return print(invoice.customer, rowsToPrint, convertToHtml);
    }

    private String print(String customer, List<TableRow> rowsToPrint, boolean convertToHtml) {
        final NumberFormat frmt = NumberFormat.getCurrencyInstance(Locale.US);
        var totalAmount = getTotalAmount(rowsToPrint);
        var volumeCredits = getVolumeCredits(rowsToPrint);
        final StringBuilder sb = new StringBuilder();
        if (convertToHtml) {
            sb.append(String.format("<h1>Statement for %s</h1>\n", customer));
            if (!rowsToPrint.isEmpty()) {
                sb.append("""
                        <table>
                        <tr>
                          <th>play</th>
                          <th>seats</th>
                          <th>cost</th>
                        </tr>
                        """);
                for (TableRow row : rowsToPrint) {
                    sb.append(String.format("""
                                            <tr>
                                              <td>%s</td>
                                              <td>%s</td>
                                              <td>%s</td>
                                            </tr>
                                                """, row.playName, frmt.format(row.playCost / 100), row.audienceCount));
                }
                sb.append("</table>\n");
            }
            sb.append(String.format("""
                              <p>
                              Amount owed is <em>%s</em>
                              </p>
                              <p>
                              You earned <em>%s</em> credits
                              </p>
                              """, frmt.format(totalAmount / 100), volumeCredits));

        } else {
            sb.append(String.format("Statement for %s\n", customer));
            if (!rowsToPrint.isEmpty()) {
                for (TableRow row : rowsToPrint) {
                    sb.append(String.format("  %s: %s (%s seats)\n", row.playName, frmt.format(row.playCost / 100), row.audienceCount));
                }
            }
            sb.append(String.format("Amount owed is %s\n", frmt.format(totalAmount / 100)));
            sb.append(String.format("You earned %s credits\n", volumeCredits));
        }
        return sb.toString();
    }

    private List<TableRow> calculateAndReturnTableRows(Invoice invoice, Map<String, Play> plays) {
        final List<TableRow> rows = new ArrayList<>();
        for (var perf : invoice.performances) {
            var play = plays.get(perf.playID);
            var thisAmount = 0;
            switch (play.type) {
                case "tragedy":
                    thisAmount = 40000;
                    if (perf.audience > 30) {
                        thisAmount += 1000 * (perf.audience - 30);
                    }
                    break;
                case "comedy":
                    thisAmount = 30000;
                    if (perf.audience > 20) {
                        thisAmount += 10000 + 500 * (perf.audience - 20);
                    }
                    thisAmount += 300 * perf.audience;
                    break;
                default:
                    throw new Error("unknown type: ${play.type}");
            }
            rows.add(new TableRow(play.name, play.type, thisAmount, perf.audience));
        }
        return rows;
    }

    private int getTotalAmount(List<TableRow> rows) {
        return rows.stream().map(TableRow::playCost).reduce(0, Integer::sum);
    }

    private int getVolumeCredits(List<TableRow> rows) {
        int volumeCredits = 0;
        for (TableRow row : rows) {
            // add volume credits
            volumeCredits += Math.max(row.audienceCount - 30, 0);
            // add extra credit for every ten comedy attendees
            if ("comedy".equals(row.playType)) volumeCredits += Math.floor(row.audienceCount / 5);
        }
        return volumeCredits;
    }

    record TableRow(String playName, String playType, int playCost, int audienceCount) {}

}
