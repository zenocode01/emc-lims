import { useRef, useState } from 'react'
import { Button, Space, Tag, Modal, message, Descriptions, Timeline, Card, Drawer } from 'antd'
import {
  PlusOutlined,
  DeleteOutlined,
  HistoryOutlined,
  EyeOutlined,
  ArrowRightOutlined,
  ExclamationCircleOutlined,
} from '@ant-design/icons'
import type { ProColumns, ActionType } from '@ant-design/pro-components'
import { ProTable } from '@ant-design/pro-components'
import { sampleApi, type SampleVO, type SampleQuery, SAMPLE_STATUS_OPTIONS, SAMPLE_STATUS_COLOR } from '../../api/sample'
import SampleForm from './SampleForm'

/**
 * 样品管理页面
 */
export default function SamplePage() {
  const actionRef = useRef<ActionType>(null)
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([])
  const [formOpen, setFormOpen] = useState(false)
  const [formData, setFormData] = useState<SampleVO | undefined>(undefined)

  /** 详情抽屉 */
  const [detailOpen, setDetailOpen] = useState(false)
  const [detailData, setDetailData] = useState<SampleVO | undefined>(undefined)

  /** 流转日志抽屉 */
  const [logDrawerOpen, setLogDrawerOpen] = useState(false)
  const [logData, setLogData] = useState<SampleVO | undefined>(undefined)
  const [logs, setLogs] = useState<any[]>([])

  /** 状态变更记录 */
  const [statusHistory, setStatusHistory] = useState<any[]>([])

  /** 新增 */
  const handleAdd = () => {
    setFormData(undefined)
    setFormOpen(true)
  }

  /** 编辑 */
  const handleEdit = (record: SampleVO) => {
    setFormData(record)
    setFormOpen(true)
  }

  /** 查看详情 */
  const handleDetail = async (record: SampleVO) => {
    setDetailData(record)
    setDetailOpen(true)
    // 获取详情（含关联信息）
    try {
      const res = await sampleApi.detail(record.id)
      setDetailData(res)
    } catch {
      // error handled by interceptor
    }
  }

  /** 查看流转日志 */
  const handleLogs = async (record: SampleVO) => {
    setLogData(record)
    setLogDrawerOpen(true)
    try {
      const logsRes = await sampleApi.logs(record.id)
      setLogs(logsRes)
    } catch {
      setLogs([])
    }
  }

  /** 删除 */
  const handleDelete = async (ids: number[]) => {
    Modal.confirm({
      title: '确认删除',
      icon: <ExclamationCircleOutlined />,
      content: `确定删除选中的 ${ids.length} 个样品吗？删除后不可恢复。`,
      okText: '确认删除',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        try {
          await sampleApi.deleteBatch(ids)
          message.success('删除成功')
          actionRef.current?.reload()
          setSelectedRowKeys([])
        } catch {
          // error handled by interceptor
        }
      },
    })
  }

  /** 表单成功回调 */
  const handleFormSuccess = () => {
    setFormOpen(false)
    setFormData(undefined)
    actionRef.current?.reload()
  }

  /** 获取状态标签 */
  const getStatusTag = (status: string) => {
    const option = SAMPLE_STATUS_OPTIONS.find((o) => o.value === status)
    const color = SAMPLE_STATUS_COLOR[status] || 'default'
    return <Tag color={color}>{option?.label || status}</Tag>
  }

  /** 状态变更 */
  const handleChangeStatus = async (record: SampleVO, toStatus: string) => {
    Modal.confirm({
      title: '确认状态变更',
      icon: <ExclamationCircleOutlined />,
      content: `确定将样品 ${record.sampleNo} 的状态变更为"${SAMPLE_STATUS_OPTIONS.find((o) => o.value === toStatus)?.label || toStatus}"吗？`,
      okText: '确认',
      cancelText: '取消',
      onOk: async () => {
        try {
          await sampleApi.changeStatus({
            sampleId: record.id,
            toStatus,
          })
          message.success('状态变更成功')
          actionRef.current?.reload()
          // 刷新详情
          if (detailData?.id === record.id) {
            const res = await sampleApi.detail(record.id)
            setDetailData(res)
          }
        } catch {
          // error handled by interceptor
        }
      },
    })
  }

  /** 状态变更按钮渲染 */
  const renderStatusActions = (record: SampleVO) => {
    const actions: { label: string; toStatus: string; color?: string }[] = []

    switch (record.status) {
      case 'pending':
        actions.push({ label: '收样登记', toStatus: 'received' })
        break
      case 'received':
        actions.push({ label: '开始测试', toStatus: 'testing' })
        break
      case 'testing':
        actions.push({ label: '测试完成', toStatus: 'completed' })
        break
      case 'completed':
        actions.push(
          { label: '留样', toStatus: 'retained' },
          { label: '处置', toStatus: 'disposed' },
          { label: '归还', toStatus: 'returned' },
        )
        break
      case 'retained':
        actions.push({ label: '处置', toStatus: 'disposed' })
        break
      default:
        break
    }

    return actions.map((action) => (
      <a key={action.toStatus} onClick={() => handleChangeStatus(record, action.toStatus)}>
        {action.label}
      </a>
    ))
  }

  const columns: ProColumns<SampleVO>[] = [
    {
      title: '样品编号',
      dataIndex: 'sampleNo',
      width: 160,
      search: false,
      render: (_, record) => <a onClick={() => handleDetail(record)}>{record.sampleNo}</a>,
    },
    {
      title: '产品名称',
      dataIndex: 'productName',
      width: 150,
      ellipsis: true,
      search: false,
    },
    {
      title: '客户',
      dataIndex: 'customerName',
      width: 120,
      search: false,
    },
    {
      title: '型号',
      dataIndex: 'model',
      width: 120,
      ellipsis: true,
      search: false,
    },
    {
      title: '收样日期',
      dataIndex: 'receiveDate',
      width: 120,
      valueType: 'date',
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      valueType: 'select',
      valueEnum: Object.fromEntries(
        SAMPLE_STATUS_OPTIONS.map((o) => [o.value, { text: o.label, status: SAMPLE_STATUS_COLOR[o.value] === 'processing' ? 'Processing' : (SAMPLE_STATUS_COLOR[o.value] === 'success' ? 'Success' : (SAMPLE_STATUS_COLOR[o.value] === 'warning' ? 'Warning' : 'Default')) }]),
      ),
      search: false,
      render: (_, record) => getStatusTag(record.status),
    },
    {
      title: '测试工程师',
      dataIndex: 'testerName',
      width: 100,
      search: false,
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      width: 170,
      valueType: 'dateTime',
      search: false,
      sorter: true,
    },
    {
      title: '操作',
      width: 240,
      fixed: 'right',
      search: false,
      render: (_, record) => (
        <Space>
          <a onClick={() => handleDetail(record)}>
            <EyeOutlined /> 详情
          </a>
          <a onClick={() => handleLogs(record)}>
            <HistoryOutlined /> 日志
          </a>
          <a onClick={() => handleEdit(record)}>编辑</a>
          {renderStatusActions(record)}
        </Space>
      ),
    },
  ]

  return (
    <Card>
      <ProTable<SampleVO, SampleQuery>
        actionRef={actionRef}
        columns={columns}
        rowKey="id"
        request={async (params) => {
          const { current, pageSize, ...rest } = params
          const res = await sampleApi.page({
            pageNum: current,
            pageSize,
            keyword: rest.keyword,
            customerId: rest.customerId,
            status: rest.status,
            receiveDateStart: rest.receiveDateStart,
            receiveDateEnd: rest.receiveDateEnd,
          })
          return {
            data: res.records,
            total: res.total,
            success: true,
          }
        }}
        toolbar={{
          actions: [
            <Button key="add" type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
              新增样品
            </Button>,
            <Button
              key="delete"
              danger
              icon={<DeleteOutlined />}
              disabled={selectedRowKeys.length === 0}
              onClick={() => handleDelete(selectedRowKeys as number[])}
            >
              批量删除
            </Button>,
          ],
        }}
        rowSelection={{
          selectedRowKeys,
          onChange: (keys) => setSelectedRowKeys(keys),
        }}
        pagination={{
          showSizeChanger: true,
          showQuickJumper: true,
          pageSizeOptions: ['10', '20', '50'],
        }}
        search={{
          labelWidth: 'auto',
          defaultCollapsed: true,
        }}
        dateFormatter="string"
      />

      {/* 新增/编辑弹窗 */}
      <SampleForm
        open={formOpen}
        data={formData}
        onSuccess={handleFormSuccess}
        onCancel={() => {
          setFormOpen(false)
          setFormData(undefined)
        }}
      />

      {/* 详情抽屉 */}
      <Drawer
        title={`样品详情 - ${detailData?.sampleNo || ''}`}
        placement="right"
        width={700}
        open={detailOpen}
        onClose={() => setDetailOpen(false)}
        extra={
          <Space>
            <Button onClick={() => { if (detailData) handleEdit(detailData) }}>编辑</Button>
            <Button onClick={() => { if (detailData) handleLogs(detailData) }}>
              <HistoryOutlined /> 流转日志
            </Button>
          </Space>
        }
      >
        {detailData && (
          <>
            <Descriptions bordered column={2} size="small">
              <Descriptions.Item label="样品编号" span={1}>{detailData.sampleNo}</Descriptions.Item>
              <Descriptions.Item label="状态" span={1}>{getStatusTag(detailData.status)}</Descriptions.Item>
              <Descriptions.Item label="产品名称" span={1}>{detailData.productName}</Descriptions.Item>
              <Descriptions.Item label="型号" span={1}>{detailData.model || '-'}</Descriptions.Item>
              <Descriptions.Item label="客户" span={1}>{detailData.customerName || '-'}</Descriptions.Item>
              <Descriptions.Item label="生产厂家" span={1}>{detailData.manufacturer || '-'}</Descriptions.Item>
              <Descriptions.Item label="批号/序列号" span={1}>{detailData.batchNo || '-'}</Descriptions.Item>
              <Descriptions.Item label="收样日期" span={1}>{detailData.receiveDate}</Descriptions.Item>
              <Descriptions.Item label="样品数量" span={1}>{detailData.sampleCount}</Descriptions.Item>
              <Descriptions.Item label="测试工程师" span={1}>{detailData.testerName || '-'}</Descriptions.Item>
              <Descriptions.Item label="测试标准" span={2}>{detailData.testStandards || '-'}</Descriptions.Item>
              <Descriptions.Item label="测试要求" span={2}>{detailData.testRequirements || '-'}</Descriptions.Item>
              <Descriptions.Item label="备注" span={2}>{detailData.remark || '-'}</Descriptions.Item>
              <Descriptions.Item label="创建时间" span={1}>{detailData.createTime}</Descriptions.Item>
              <Descriptions.Item label="更新时间" span={1}>{detailData.updateTime}</Descriptions.Item>
            </Descriptions>

            {/* 状态变更记录 */}
            <Descriptions
              title="状态变更"
              size="small"
              bordered
              style={{ marginTop: 24 }}
            />
          </>
        )}
      </Drawer>

      {/* 流转日志抽屉 */}
      <Drawer
        title={`流转日志 - ${logData?.sampleNo || ''}`}
        placement="right"
        width={600}
        open={logDrawerOpen}
        onClose={() => setLogDrawerOpen(false)}
      >
        {logs.length > 0 ? (
          <Timeline
            items={logs.map((log) => ({
              color: SAMPLE_STATUS_COLOR[log.toStatus] || 'blue',
              children: (
                <div>
                  <div style={{ fontWeight: 500 }}>
                    {log.fromStatusName ? `${log.fromStatusName} → ` : ''}
                    {log.toStatusName}
                  </div>
                  {log.remark && <div style={{ color: '#666', fontSize: 12, marginTop: 4 }}>{log.remark}</div>}
                  <div style={{ color: '#999', fontSize: 12, marginTop: 4 }}>
                    {log.operatorName || '系统'} · {log.createTime}
                  </div>
                </div>
              ),
            }))}
          />
        ) : (
          <div style={{ textAlign: 'center', color: '#999', padding: '40px 0' }}>暂无流转日志</div>
        )}
      </Drawer>
    </Card>
  )
}
