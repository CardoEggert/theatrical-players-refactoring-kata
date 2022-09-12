package kata;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

public class StatementPrinter {

    public String print(Invoice invoice, Map<String, Play> plays) {
        return print(invoice, plays, false);
    }

    public String print(Invoice invoice, Map<String, Play> plays, boolean convertToHtml) {
        final StringBuilder sb = new StringBuilder();
        var totalAmount = 0;
        var volumeCredits = 0;
        if (convertToHtml) {
            sb.append(String.format("<h1>Statement for %s</h1>\n", invoice.customer));
        } else {
            sb.append(String.format("Statement for %s\n", invoice.customer));
        }

        NumberFormat frmt = NumberFormat.getCurrencyInstance(Locale.US);

        if (!invoice.performances.isEmpty()) {
            if (convertToHtml) {
                sb.append("""
                            <table>
                            <tr>
                              <th>play</th>
                              <th>seats</th>
                              <th>cost</th>
                            </tr>
                            """);
            }
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

                // add volume credits
                volumeCredits += Math.max(perf.audience - 30, 0);
                // add extra credit for every ten comedy attendees
                if ("comedy".equals(play.type)) volumeCredits += Math.floor(perf.audience / 5);

                // print line for this order
                if (convertToHtml) {
                    sb.append(String.format("""
                                                <tr>
                                                  <td>%s</td>
                                                  <td>%s</td>
                                                  <td>%s</td>
                                                </tr>
                                                    """, play.name, frmt.format(thisAmount / 100), perf.audience));
                } else {
                    sb.append(String.format("  %s: %s (%s seats)\n", play.name, frmt.format(thisAmount / 100), perf.audience));
                }
                totalAmount += thisAmount;
            }
            if (convertToHtml) {
                sb.append("</table>\n");
            }
        }
        if (convertToHtml) {
            sb.append(String.format("""
                              <p>
                              Amount owed is <em>%s</em>
                              </p>
                              <p>
                              You earned <em>%s</em> credits
                              </p>
                              """, frmt.format(totalAmount / 100), volumeCredits));
        } else {
            sb.append(String.format("Amount owed is %s\n", frmt.format(totalAmount / 100)));
            sb.append(String.format("You earned %s credits\n", volumeCredits));
        }
        return sb.toString();
    }

}
