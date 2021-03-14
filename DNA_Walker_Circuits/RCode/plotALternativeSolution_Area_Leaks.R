folderName = "E:\\SA\\Toy\\NewGridSize_WithAlternativeSolutions\\"

for(i in 1:20){
  
  dat <- read.table(paste0(folderName,"Run",i,"\\SolutionsLog.txt"), 
                    header = T, stringsAsFactors = F)
  
  newSolutions = dat[dat$FileName %like% "SA_NewSol_Iteration_",]
  
  newSolutions$Leaks <- paste(newSolutions$Short_leaks,
                              newSolutions$Medium_leaks,
                              newSolutions$Long_leaks,sep = ",")
  
  
  newSolutions <- newSolutions[,-c(1,3,4,5,8)]
  
  newSolutions <- unique(newSolutions[,1:4])
  
  fitnessValues <- unique(newSolutions$Fitness)
  
  for(v in 1:length(fitnessValues)){
  
    temp <- newSolutions[newSolutions$Fitness == fitnessValues[v],]
    
    p <- ggplot(data = temp, aes(x = as.factor(Leaks), y = Area))+
      geom_point(aes(size = 2)) +
      ggtitle(paste0("Toy Layout - Run ",i," - Fitness ",fitnessValues[v],
                     " - Unique Solutions ", nrow(temp))) +
      xlab("Leaks") +
      ylab("Area") 
    
    ggsave(paste0(folderName,"RunStats\\FitnessAreaLeaks\\Run_",i,"_fitnessAreaLeakDistribution.jpg"), 
           p , height = 7, width = 10)
    
    temp <- NULL
    
  }

  
}