package com.bustoskaiciuokle.back;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.chart.XYChart.Data;
import java.io.FileWriter;
import java.io.IOException;

final public class Calculator {
    public Calculator() {
        m_monthMin = 0;
        m_monthMax = Integer.MAX_VALUE;
        m_atidejimasNuo = -1;
        m_atidejimoLaikotarpis = 0;
        atidejimoProcentas = 0;
    }
    public enum GraphType {
        ANUITETO, LINIJINIS
    }
    public void process() {
        if (m_atidejimasNuo > metai * 12 + menesiai || m_atidejimasNuo < 0) {
            m_atidejimoLaikotarpis = 0;
        }
        int monthCount = metai * 12 + menesiai + m_atidejimoLaikotarpis;
        m_monthMin = Math.min(m_monthMin, monthCount - 1);
        m_monthMax = Math.min(m_monthMax, monthCount - 1);
        m_monthMin = Math.min(m_monthMin, m_monthMax);
        m_monthMax = Math.max(m_monthMax, m_monthMin);

        updateData();
    }
    public void setMonthMin(int monthMin) {
        m_monthMin = monthMin;   
    }
    public void setMonthMax(int monthMax) {
        m_monthMax = monthMax;   
    }
    public int getMonthCount() {
        return metai * 12 + menesiai;
    }
    public void setAtidejimasNuo(int atidejimasNuo) {
        m_atidejimasNuo = atidejimasNuo;
    }
    public void setAtidejimoLaikotarpis(int atidejimoLaikotarpis) {
        m_atidejimoLaikotarpis = atidejimoLaikotarpis;
    }
    public int getAtidejimoLaikotarpis() {
        return m_atidejimoLaikotarpis;
    }
    public ObservableList<TableCellData> getTableData() {
        return m_tableData;
    }
    public Series<Number, Number> getGraphData() {
        return m_graphData;
    }

    private void updateData() {
        if (m_tableData == null) {
            m_tableData = FXCollections.observableArrayList();
        }
        m_tableData.clear();
        if (m_graphData == null) {
            m_graphData = new Series<>();
        }
        m_graphData.getData().clear();
        int monthCount = metai * 12 + menesiai;
        
        if (graphType == GraphType.ANUITETO) {
            double palukanosPerMen = procentai / 12 / 100;
            double temp = Math.pow((1 + palukanosPerMen), monthCount);
            double payPerMonth = (palukanosPerMen * temp) / (temp - 1) * paskolosSuma;
            double payAmountLeft = payPerMonth * monthCount;
            for (int i = 0; i <= monthCount + m_atidejimoLaikotarpis; i++) {
                double newPayAmount = payPerMonth;
                double newPayAmountPalukanos = 0;
                if (i >= m_atidejimasNuo && i < m_atidejimasNuo + m_atidejimoLaikotarpis) {
                    newPayAmount = paskolosSuma * (atidejimoProcentas / 12 / 100);
                    newPayAmountPalukanos = 0;
                }
                if (i >= m_monthMin && i <= m_monthMax) {
                    TableCellData data = new TableCellData();
                    data.month = i + 1;
                    data.payAmount = newPayAmount;
                    data.payAmountPalukanos = newPayAmountPalukanos;
                    data.payAmountLeft = payAmountLeft;
                    m_tableData.add(data);
                }
                m_graphData.getData().add(new Data<>(i + 1, newPayAmount + newPayAmountPalukanos));
                
                if (i < m_atidejimasNuo || i >= m_atidejimasNuo + m_atidejimoLaikotarpis) {
                    payAmountLeft -= payPerMonth;
                }
            }

        } else if (graphType == GraphType.LINIJINIS) {
            double base = paskolosSuma / monthCount;
            double palukanos = paskolosSuma * (procentai / 12 / 100);
            double payAmountLeft = paskolosSuma;
            for (int i = 0; i < monthCount + m_atidejimoLaikotarpis; i++) {
                double newPayAmount = base;
                double newPayAmountPalukanos = palukanos;
                if (i >= m_atidejimasNuo && i < m_atidejimasNuo + m_atidejimoLaikotarpis) {
                    newPayAmount = 0;
                    newPayAmountPalukanos = paskolosSuma * (atidejimoProcentas / 12 / 100);
                }
                if (i >= m_monthMin && i <= m_monthMax) {
                    TableCellData data = new TableCellData();
                    data.month = i + 1;
                    data.payAmount = newPayAmount;
                    data.payAmountPalukanos = newPayAmountPalukanos;
                    data.payAmountLeft = payAmountLeft;
                    m_tableData.add(data);
                }
                m_graphData.getData().add(new Data<>(i + 1, newPayAmount + newPayAmountPalukanos));
                if (i < m_atidejimasNuo || i >= m_atidejimasNuo + m_atidejimoLaikotarpis) {
                    payAmountLeft -= base;
                    palukanos = payAmountLeft * (procentai / 12 / 100);
                } 
            }
        }
    }
    public boolean saveToFile(String fileName) {
        try {
            FileWriter writer = new FileWriter(fileName);
            // find longest strings
            int longestMonth = 0;
            int longestPayAmount = 0;
            int longestPayAmountLeft = 0;
            for (TableCellData data : m_tableData) {
                longestMonth = Math.max(longestMonth, String.valueOf(data.month).length());
                longestPayAmount = Math.max(longestPayAmount, String.format("%.2f", data.payAmount).length());
                longestPayAmountLeft = Math.max(longestPayAmountLeft, String.format("%.2f", data.payAmountLeft).length());
            }
            longestMonth = Math.max(longestMonth, "Mėnuo".length()) + 2;
            longestPayAmount = Math.max(longestPayAmount, "Įmoka".length()) + 2;
            longestPayAmountLeft = Math.max(longestPayAmountLeft, "Liko mokėti".length()) + 2;
            
            // write header
            writer.write(String.format("%-" + longestMonth + "s %-" + longestPayAmount + "s %-" + longestPayAmountLeft + "s\n", "Mėnuo", "Įmoka", "Liko mokėti"));

            // write data
            for (TableCellData data : m_tableData) {
                writer.write(String.format("%-" + longestMonth + "d %-" + longestPayAmount + ".2f %-" + longestPayAmountLeft + ".2f\n", data.month, data.payAmount, data.payAmountLeft));
            }

            writer.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    final public class TableCellData {
        public double getPayAmount() {
            return payAmount;
        }
        public double getPayAmountPalukanos() {
            return payAmountPalukanos;
        }
        public double getPayAmountLeft() {
            return payAmountLeft;
        }
        public int getMonth() {
            return month;
        }
        private double payAmount;
        private double payAmountPalukanos;
        private double payAmountLeft;
        private int month;
    }
    public double paskolosSuma;
    public double procentai;
    public double atidejimoProcentas;
    public int metai;
    public int menesiai;
    public GraphType graphType;

    private int m_atidejimasNuo;
    private int m_atidejimoLaikotarpis;
    private int m_monthMin;
    private int m_monthMax;
    private ObservableList<TableCellData> m_tableData;
    private Series<Number, Number> m_graphData;
}
