
#fN = "E:\\SA\\Toy\\NewGridSize_10000I\\" 
fN = "D:\\SA\\Toy\\NewGridSize_WithAlternativeSolutions\\"

overAllElitef <- data.frame()

for(x in 1:20){
  
  bestSolFile = paste0(fN,"Run",x,"\\BestSolutionsLog.txt")
  
  d = read.table(bestSolFile, header = T, stringsAsFactors = F, row.names = NULL)
  
  colnames(d) <- c("Iteration", "Fit", "Elitef",	"Elitex",
                  "InitialSolution",	"NewSolution", "Temperature")
  
  overAllElitef <- rbind(overAllElitef, data.frame(Run = x, BestFitness = min(d$Elitef)))
  
}

library(plyr)
library(ggplot2)

y <- count(overAllElitef$BestFitness)


p <- ggplot(data=y, aes(x=as.factor(x), y=freq)) +
  geom_bar(stat="identity", fill="steelblue")+
  theme_minimal() +
  ggtitle("Toy layout - SA 10000 iterations - 20 runs") +
  xlab("Best Fitness Values Observed in the 20 runs") +
  ylab("Frequency") +
  geom_text(aes(label=freq),hjust=0, vjust=0)

pName = paste0(fN,"RunStats\\BestFitnessFrequencyT.jpg")

ggsave(pName, plot=p, height = 6, width = 10, units = "in")