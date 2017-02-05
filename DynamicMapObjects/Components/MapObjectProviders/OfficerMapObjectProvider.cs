using Genetec.Sdk;
using Genetec.Sdk.Entities;
using Genetec.Sdk.Entities.Maps;
using Genetec.Sdk.Queries;
using Genetec.Sdk.Workspace;
using Genetec.Sdk.Workspace.Components.MapObjectProvider;
using System;
using System.Collections.Generic;
using System.Data;
using System.Globalization;
using Stream = System.IO.Stream;
using System.Linq;
using System.Reflection;
using System.Threading;
using System.Threading.Tasks;
using System.Xml.Linq;
using DynamicMapObjects.Maps;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;

// ==========================================================================
// Copyright (C) 2015 by Genetec, Inc.
// All rights reserved.
// May be used only in accordance with a valid Source Code License Agreement.
// ==========================================================================
namespace DynamicMapObjects.Components.MapObjectProviders
{
    #region Classes
    public class AlarmQuery
    {
        [JsonProperty("ID")]
        public int Id { get; set; }

        [JsonProperty("latitude")]
        public double latitude { get; set; }

        [JsonProperty("longitude")]
        public double longitude { get; set; }

        [JsonProperty("ack")]
        public int ack { get; set; }

        [JsonProperty("respondent")]
        public string respondent { get; set; }
    }

    public class Guard
    {
        [JsonProperty("ID")]
        public string Id { get; set; }

        [JsonProperty("latitude")]
        public double latitude { get; set; }

        [JsonProperty("longitude")]
        public double longitude { get; set; }
    }

    public sealed class OfficerMapObjectProvider : MapObjectProvider, IDisposable
    {
        #region Constants

        private List<OfficerObject> m_officerList;
        private List<AlarmObject> m_alarmList;
        #endregion

        #region Fields

        private bool m_moveOfficers = true;

        private System.Net.WebClient client;
        private JsonSerializer jsonSerializer;

        #endregion

        #region Properties

        /// <summary>
        /// Gets the name of the component
        /// </summary>
        public override string Name
        {
            get { return "Officers map object provider"; }
        }

        /// <summary>
        /// Gets the unique identifier of the component
        /// </summary>
        public override Guid UniqueId
        {
            get { return new Guid("{5DFE38E1-6920-4728-A514-ACA4135EDCA6}"); }
        }

        #endregion

        #region Constructors

        public OfficerMapObjectProvider(Workspace workspace)
        {
            Initialize(workspace);
            workspace.Sdk.LoggedOn += OnProxyLoggedOn;
        }

        #endregion

        #region Destructors and Dispose Methods

        public void Dispose()
        {
            m_moveOfficers = false;
        }

        #endregion

        #region Event Handlers

        private void OnProxyLoggedOn(object sender, LoggedOnEventArgs loggedOnEventArgs)
        {
            client = new System.Net.WebClient();
            jsonSerializer = new JsonSerializer();
            m_officerList = new List<OfficerObject>();
            m_alarmList = new List<AlarmObject>();
            Task.Run(() => Update());
        }

        private void Update()
        {
            while (true)
            {
                m_officerList.Clear();
                m_alarmList.Clear();
                var text = client.DownloadString("https://1hiutgaba7.execute-api.us-east-1.amazonaws.com/prod/GetGuards");
                List<Guard> results = JsonConvert.DeserializeObject<List<Guard>>(text);
                foreach (Guard guard in results)
                {
                    OfficerObject oObject;
                    var officer = new OfficerMapObject();
                    oObject = new OfficerObject(officer, new GeoCoordinate(guard.latitude, guard.longitude));
                    oObject.ID = guard.Id;
                    m_officerList.Add(oObject);
                }
                text = client.DownloadString("https://1hiutgaba7.execute-api.us-east-1.amazonaws.com/prod/GetAlarms");
                List<AlarmQuery> resultsAlarm = JsonConvert.DeserializeObject<List<AlarmQuery>>(text);
                foreach (AlarmQuery alarmQuery in resultsAlarm)
                {
                    AlarmObject oObject;
                    var alarmMapObj = new AlarmCustomMapObject();
                    oObject = new AlarmObject(alarmMapObj, new GeoCoordinate(alarmQuery.latitude, alarmQuery.longitude));
                    oObject.ID = alarmQuery.Id.ToString();
                    m_alarmList.Add(oObject);
                }
                Thread.Sleep(500);
            }
        }

        #endregion

        #region Public Methods

        public override IList<MapObject> Query(Guid mapId, GeoBounds viewArea)
        {
            var map = Workspace.Sdk.GetEntity(mapId) as Map;

            // Provide officers for geo referenced maps
            if ((map != null) && map.IsGeoReferenced)
            {
                var result = new List<MapObject>();
                result.AddRange(m_officerList.Select(i => i.Officer)); // Here, the viewArea could be used to return only the MapObjects inside the displayed bounds
                return result;
            }

            return null;
        }

        #endregion

        #region Private Methods

        #endregion
    }

    #endregion
}

