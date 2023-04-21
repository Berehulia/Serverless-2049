async function fetchData() {
  const response = await fetch("data_aws.json");
  return await response.json();
}

let sourceData = await fetchData();

function prepareDatasetsFoScenario(sourceDataForScenario, extractDataFunc) {
  const uniqueLabels = new Set();
  const datasets = [];
  for (const functionData of sourceDataForScenario.data) {
    let dataset = {
      label: functionData.function_name,
      data: []
    }
    for (const measurement of functionData['measurements']) {
      uniqueLabels.add(measurement.value);
      dataset.data.push(extractDataFunc(measurement))
    }
    datasets.push(dataset);
  }
  return {
    labels: [...uniqueLabels],
    datasets: datasets
  }
}

function prepareDatasets(sourceData, extractDataFunc) {
  // TODO: handle all scenarios
  const scenario1 = prepareDatasetsFoScenario(sourceData.scenarios[0], extractDataFunc);
  return scenario1;
}


/* Example:
const labels = ['128', '256', '512', '1024', '2048', '3008'];
const data = {
  labels: labels, // x-axis
  datasets: [
    {
      label: 'Python_x86',
      data: [225.9966666666667, 100.92133333333331, 58.80716666666667, 41.26133333333333, 38.09000000000001, 38.168],
    },
        {
      label: 'Python_ARM',
      data: [undefined, 120.92133333333331, 60.80716666666667, 33.26133333333333, 30.09000000000001, undefined],
    }
  ]
}
 */



function decode(x, cls = Float32Array) {
  return Array.from(new cls(window.base64js.toByteArray(x).buffer));
}

function smartRound(x, s = 1) {
  if (x < 1e-12) {
    return x.toFixed(s).replace(/(\.0*[1-9]+)[0]+$|\.[0]+$/, '$1');
  }

  let digits = Math.max(-Math.round(Math.log10(x)) + s, 0);
  let string = x.toFixed(digits);
  return string.replace(/(\.0*[1-9]+)[0]+$|\.[0]+$/, '$1');
}


function prepareChartConfig(data, xAxisTitle, yAxisTitle, yAxisMeasurementUnits) {
  return {
  type: 'line',
  data: data,
  options: {
    responsive: true,
    stacked: false,
    tension: 0.2,
    plugins: {
      tooltip: {
        mode: 'index',
        intersect: false
      },
      legend: {
        position: "right",
      }
    },
    hover: {
      mode: 'index',
      intersec: false
    },
    scales: {
      x: {
        title: {
          display: true,
          text: xAxisTitle,
        }
      },
      y: {
        title: {
          display: true,
          text: yAxisTitle,
        },
        ticks: {
          beginAtZero: true,
          callback: (value /*, index, values*/) => `${smartRound(value)} ${yAxisMeasurementUnits}`,
        },
      }
    }
  },
}
}


new Chart(
    document.getElementById('awsFunctionsExecutionTime'),
    prepareChartConfig(
        prepareDatasets(sourceData, (obj) => obj.averageDuration,),
         'Memory / Power (MB)',
            'Invocation Time',
            'ms')
);

new Chart(
    document.getElementById('awsFunctionsPrice'),
    prepareChartConfig(
        prepareDatasets(sourceData, (obj) => obj.averagePrice,),
         'Memory / Power (MB)',
            'Price',
            '$')
);
