library(ggplot2)
library(reshape2)

setwd("D:/BellosData/Goodbadsplit_31042017")

dataPlot = read.table("MasterSummary_GoodGeneration.txt",sep = ",", header = T)

dataPlotTransposed= t(dataPlot)

#making the first row into column headers
dataPlotTransposed = setNames(data.frame(t(dataPlot[,-1])), dataPlot[,1])

#converting the row names into the first column
dataPlotTransposed = cbind(Generation = rownames(dataPlotTransposed), dataPlotTransposed)

dataPlotTransposed = cbind(Index = c(0:264),dataPlotTransposed)
dataPlotTransposed$Generation <- NULL
#plot with ggplot 

meltedData <- melt(dataPlotTransposed, id.vars="Index")

# Everything on the same plot
plotAll <- ggplot(meltedData, aes(Index,value, col=variable)) + 
  geom_point() + geom_line() +
  ggtitle("Occurance of each gene per generation (Good Gen)") +
  labs(x = "Generation", y = "Gene Count") +
  theme(legend.position="none") 

ggsave(filename = "AllInOne_GoodGen.png", plot=plotAll, width = 15 , height = 7)


#with legend
plotAll2 <- ggplot(meltedData, aes(Index,value, col=variable)) + 
  geom_point() + geom_line() +
  ggtitle("Occurance of each gene per generation (Good Gen)") +
  labs(x = "Generation", y = "Gene Count")

ggsave(filename = "AllInOne_GoodGen_withLegend.png", plot=plotAll2, width = 15 , height = 7)



# Separate plots
plotSeparate <- ggplot(meltedData, aes(Index,value)) + 
  geom_point(size = 1, color = "blue") + geom_line(linetype="dotted", color="blue", size=1) +
  #geom_smooth() +
  facet_wrap(~variable) +
  scale_color_manual(values=c("#CC6666")) +
  ggtitle("Occurance of each gene per generation (Good Gen)") +
  labs(x = "Generation", y = "Gene Count")

ggsave(filename = "AllInSeperate__GoodGen.png", plot = plotSeparate, width = 17 , height = 10)


