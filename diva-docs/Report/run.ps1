pandoc .\metadata.yml (Get-ChildItem ??_*.md | convert-path) -c .\buttondown.css -H header-includes.tex --mathjax --filter=pandoc-crossref --filter=pandoc-citeproc --toc  --toc-depth=2 -sS --template template.tex -t latex -o test.pdf -N

