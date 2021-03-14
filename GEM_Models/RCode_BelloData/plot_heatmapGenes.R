#create bar plot and heat map for gene data
setwd("D:/BellosData/GC_3")
library(stringr)
library(ggplot2)
library(reshape2)
library(gtools)

generationFiles = list.files(pattern = "^Generation.*\\_\\d+.txt$")

for(a in 1:length(generationFiles)){
  a = 1
  fname = substr(generationFiles[a],1,nchar(generationFiles[a])-4)
  
  geneData = read.table(generationFiles[a], sep = "\n", header = T, stringsAsFactors = F)
  
  geneCount = str_count(geneData[1,1],"[1|0]")
  
  geneDF <- data.frame(Gene = paste0("Gene_", c(1:geneCount)), stringsAsFactors = F)
  
  cummilativeGeneDF = data.frame()
  
  for(i in 1:nrow(geneData)){
    
    #remove the square brackets
    dataRow = substring(geneData[i,1], 2, nchar(geneData[i,1]) - 1)
    
    #removing white spaces
    dataRow = gsub(" ", "", dataRow, fixed = T)
    
    #splitting the string 
    dataRow = strsplit(dataRow, ",")
    
    #converting to numeric
    dataRow = as.numeric(unlist(dataRow))
    
    colName = paste("M_",i,sep = "")
    geneDF <- cbind(geneDF, dataRow)
    colnames(geneDF)[colnames(geneDF) == 'dataRow'] <- colName
    
    dataRow <- NULL
  }
  
  #bar plot
  geneDFColSum = data.frame(colnames(geneDF[,c(2:450)]),colSums(geneDF[,c(2:450)]), stringsAsFactors = F)
  colnames(geneDFColSum) <- c("Model", "Count")
  
  geneDFColSum$Model = factor(geneDFColSum$Model, levels = gtools::mixedsort(geneDFColSum$Model))
  
 
  #png(paste0("D://BellosData//GC_3//barplots//",fname,"_barplot.png"),width = 1500, height = 700)
  
  p<-ggplot(data=geneDFColSum, aes(x=Model, y=Count)) +
    geom_bar(stat="identity", fill="steelblue")+
    theme_minimal()+
    ylab("No of present genes ") +
    xlab("Model") +
    ggtitle(fname)+
    theme(axis.text.x = element_text(angle = 90, hjust = 1, size = 5))
  
  #dev.off()
  ggsave(p,filename=paste0("D://BellosData//GC_3//barplots//",fname,"_barplot.png"), width = 15,
         height = 7)
  
  #heat map
  geneDFMelt = reshape2::melt(geneDF, id.vars = "Gene")
  names(geneDFMelt)[2:3] <- c("Model", "Value")
  
  Gene_order <- mixedsort(geneDF$Gene)
  
  
  p1<- ggplot(data = geneDFMelt, aes(x = Model, y = factor(Gene, level = Gene_order))) +
    geom_tile(aes(fill = Value), color = "white") +
    scale_fill_gradient(low = "white", high = "steelblue") +
    ylab("List of Genes ") +
    xlab("List of Models") +
    ggtitle(paste0(fname," HeatMap"))+
    theme(plot.title = element_text(size=10),
          axis.title=element_text(size=8),
          axis.text.x = element_text(angle = 90, hjust = 1, size = 5),
          axis.text.y = element_text(size = 5),
          legend.position = "none")

  ggsave(p1,filename=paste0("D://BellosData//GC_3//heatmaps//",fname,"_heatmap.png"), width = 15,
         height = 7)
  
  cummilativeGeneDF <- geneDF
  
  geneDF <- NULL
  geneData <- NULL
}

write.table(cummilativeGeneDF, "CummilativeGeneDF.txt", sep = ",", quote = F, row.names = F)

