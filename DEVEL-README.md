# Developers guide

## Releasing the new version

```shell
mvn release:clean
mvn release:prepare
mvn release:perform
git pushtags
git add README.md
git commit -m "Readme version update"
git push
```