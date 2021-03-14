
#fN = "E:\\SA\\Toy\\NewGridSize_10000I\\" 
fN = "D:\\SA\\Toy\\NewGridSize_WithAlternativeSolutions\\"

overAllElitef <- data.frame("Run" = numeric(), "itr" = numeric(),
                            "sol" = character(), "fit" = numeric(), 
                            stringsAsFactors = FALSE)

for(x in 1:20){
  
  bestSolFile = paste0(fN,"Run",x,"\\AlternativeSolutionsLog.txt")
  
  d = read.table(bestSolFile, header = T, stringsAsFactors = FALSE, row.names = NULL)
  
  
  overAllElitef <- rbind(overAllElitef, data.frame(Run = x,
                                                   iter = unlist(d$Iteration),
                                                   sol = unlist(d$Solution),
                                                   fit = unlist(d$Fitness), 
                                                   stringsAsFactors = FALSE))
  

  
}

un <- unique(overAllElitef[c("sol", "fit")])



library(plyr)
library(ggplot2)
 

uny <- count(overAllElitef$fit)

uny <- uny[uny$freq > 1,]




p <- ggplot(data=uny, aes(x=as.factor(x), y=freq)) +
  geom_bar(stat="identity", fill="steelblue")+
  theme_minimal() +
  xlab("Best Fitness Values Observed in the 20 runs") +
  ylab("Frequency of Alternative solutions") +
  geom_text(aes(label=freq),hjust=0, vjust=0) +
  theme(axis.text.x = element_text(angle = 90, vjust = 0.5, hjust=1))

pName = paste0(fN,"RunStats\\AlternativSolutions_all.jpg")

ggsave(pName, plot=p, height = 6, width = 10, units = "in")