
library(data.table)
library(ggplot2)

runFolder = "E:\\SA\\Toy\\NewGridSize_WithAlternativeSolutions\\"
layoutN = "Toy"
runs = 20
iterations = 2500

for(i in 1:runs){
  i = 1
  datAlt <- read.table(paste0(runFolder,"Run",i,"\\AlternativeSolutionsLog.txt"), 
                       header = T, stringsAsFactors = F)
  
  
  uniquAltSolutions = as.data.frame(table(datAlt$Fitness))
  
  colnames(uniquAltSolutions) <- c("Fitness", "Count")
  
  p <- ggplot(data=uniquAltSolutions, aes(x=as.factor(Fitness), y=Count)) +
    geom_bar(stat="identity", fill="steelblue")+
    theme_minimal() +
    ggtitle(paste0(layoutN," Layout Alternative Solution Count - SA ",iterations, " iterations - run ",i," of " ,runs)) +
    xlab("Best Fitness Values Observed") +
    ylab("Frequency") +
    geom_text(aes(label=Count),hjust=0, vjust=0) +
    theme(axis.text.x = element_text(angle = 90, hjust = 1))
  
  pName = paste0(runFolder,"RunStats\\Run_",i,"_AlternativeSolutionsSummary.jpg")
  
  ggsave(pName, plot=p, height = 6, width = 10, units = "in")
  
  
  moreThanOnce <- uniquAltSolutions[uniquAltSolutions$Count >  1,]
  
  
  uniquAltSolutions <- NULL
  
  
  
  
}



