exit_on_failure()
{
    exit_code=${1}
    message=${2}
    if [ ${exit_code} -ne 0 ]; then
        echo "${message}, exit code:${exit_code}"
        exit ${exit_code}
    fi
}
npm run build
exit_on_failure $? "Failure while building svelte project, did you run 'npm install'?"
cp -r public tmp_public
ver=$(echo $RANDOM | md5sum | head -c 20;)
cd tmp_public/build
mv bundle.css bundle.$ver.css
mv bundle.js bundle.$ver.js
mv bundle.js.map bundle.$ver.js.map
cd ..
mv global.css global.$ver.css
sed -i -e "s/bundle/bundle.$ver/g" index.html
sed -i -e "s/global/global.$ver/g" index.html
cd ..
s3_bucket=tic-tac-toe.route42.co.il
echo Synching Build Folder: $s3_bucket...
aws s3 sync tmp_public/ s3://$s3_bucket --delete --cache-control max-age=31536000,public --profile personal

echo Adjusting cache...
# setting index.html cache to 0 age so react js file will be update every time that we upload new version
aws s3 cp s3://$s3_bucket/index.html s3://$s3_bucket/index.html --metadata-directive REPLACE --cache-control max-age=0,no-cache,no-store,must-revalidate --content-type text/html --profile personal
rm -rf tmp_public
echo DONE!


